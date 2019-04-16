package de.ingrid.mdek.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Scans the context help directory (defaults to "context_help") for markdown
 * files. Returns HTML rendered representation.
 * 
 * @author jm
 *
 */
public class MarkdownContextHelpUtils {

    protected static final String PROFILE_DIR = "_profile";

    private static final String DEFAULT_CONTEXT_HELP_PATH = "context_help";
    private static final String DEFAULT_LANG = "de";

    private final static Logger LOG = Logger.getLogger( MarkdownContextHelpUtils.class );

    private String contextHelpPath = DEFAULT_CONTEXT_HELP_PATH;
    private String defaultLanguage = DEFAULT_LANG;

    private Parser parser = null;
    private HtmlRenderer htmlRenderer = null;

    public MarkdownContextHelpUtils() {
        this.contextHelpPath = DEFAULT_CONTEXT_HELP_PATH;
        init();
    }

    public MarkdownContextHelpUtils(String contextHelpPath) {
        this.contextHelpPath = contextHelpPath;
        init();
    }

    private void init() {
        List<Extension> extensions = Arrays.asList(YamlFrontMatterExtension.create(), TablesExtension.create());        
        parser = Parser.builder().extensions( extensions ).build();
        
        List<Extension> renderExtensions = Arrays.asList(TablesExtension.create());        
        htmlRenderer = HtmlRenderer.builder().extensions(renderExtensions).build();
    }

    /**
     * Returns a Map with rendered HTML from markdown files. 
     * 
     * <p>The markdown files can be localized.</p>
     * 
     * <p>A profile mechanism exists to be able to support catalog dependent 
     * context help. This is not yet supported by the current editor but will 
     * be in the future. The idea is, that the catalogs can be configured with
     * a context help profile parameter. This parameter must match a profile 
     * directory in the context help file structure.</p>
     * 
     * <p>The context help file structure can be as follows:</p>
     * 
     * <pre> 
     * context_help
     *   _profile // profiles
     *     myprofile // profile directory
     *       en // localization directory
     *         markdownfile_en.md
     *       markdownfile.md
     *   en // localization directory
     *     markdownfile_en.md
     *   markdownfile.md
     * </pre>
     * 
     * <p>Base directory is <pre>TOMCAT/webapps/ingrid-portal-mdek-application/WEB-INF/classes/context_help</pre></p>
     * 
     * <p>Markdown files must contain front matter meta data like this to be assigned to a GUI element. 
     * The markdown can be specific to GUI element and object class.</p>
     * 
     * <pre>
     * ---
     * # ID of GUI element
     * guid: 3000
     * # optional: ID of "Objektklasse"
     * oid: 1
     * # optional: title, used as windows title
     * title: My Title
     * ---
     * </pre>
     * 
     * @return
     */
    public Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> getAvailableMarkdownHelpFiles() {

        Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> result = new HashMap<MarkdownContextHelpItemKey, MarkdownContextHelpItem>();
        
        try {
            result.putAll( getLocMarkdownHelpFilesFromPath(contextHelpPath, null));
            
            // override directory
            Path overrideDir = Paths.get( contextHelpPath, PROFILE_DIR);
            if (getClass().getClassLoader().getResource( overrideDir.toString() ) != null) {
                try (Stream<Path> profilePathStream = Files.list( Paths.get( getClass().getClassLoader().getResource( overrideDir.toString() ).toURI() ) )) {
                    
                    // read profile directories, exclude directory override
                    List<Path> profilePathList = profilePathStream.filter( Files::isDirectory ).collect( Collectors.toList() );
                    profilePathStream.close();
                    for (Path profilePath : profilePathList) {
                        String abs = profilePath.toAbsolutePath().toString();
                        String dir = abs.substring( abs.indexOf( contextHelpPath ) );
                        result.putAll( getLocMarkdownHelpFilesFromPath(dir, profilePath.getFileName().toString()));
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            LOG.error( "Impossible to get ressource from class path.", e );
            throw new RuntimeException( e );
        }
        return result;

    }
    
    private Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> getLocMarkdownHelpFilesFromPath(String sourcePath, String profile) throws IOException, URISyntaxException {
        Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> result = new HashMap<MarkdownContextHelpItemKey, MarkdownContextHelpItem>();

            result.putAll( getMarkdownHelpFilesFromPath(sourcePath, defaultLanguage, profile));
            
            try (Stream<Path> langPathStream = Files.list( Paths.get( getClass().getClassLoader().getResource( sourcePath ).toURI() ) )) {
                
                // read language directories, exclude directory override
                List<Path> langPathList = langPathStream.filter( Files::isDirectory ).filter( path -> !"override".equals(path.getFileName().toString()) ).collect( Collectors.toList() );
                langPathStream.close();
                for (Path langPath : langPathList) {
                    String abs = langPath.toAbsolutePath().toString();
                    String dir = abs.substring( abs.indexOf( sourcePath ) );
                    result.putAll( getMarkdownHelpFilesFromPath(dir, langPath.getFileName().toString(), profile));
                }
            }
            
            return result;
    }
    
    
    
    private Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> getMarkdownHelpFilesFromPath(String sourcePath, String language, String profile) throws IOException, URISyntaxException {
        Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> result = new HashMap<MarkdownContextHelpItemKey, MarkdownContextHelpItem>();

        try (Stream<Path> files = Files.list( Paths.get( getClass().getClassLoader().getResource( sourcePath ).toURI() ) )) {
            
            // read default language files
            List<Path> list = files.filter( Files::isRegularFile ).collect( Collectors.toList() );
            files.close();

            for (Path path : list) {

                String content = new String( Files.readAllBytes( Paths.get( path.toUri() ) ) );

                Node mdNode = parser.parse( content );

                YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
                mdNode.accept( visitor );

                Map<String, List<String>> data = visitor.getData();

                String guid = null;
                String oid = null;
                String title = null;

                for (Map.Entry<String, List<String>> entry : data.entrySet()) {
                    if (entry.getKey().equals( "guid" )) {
                        guid = entry.getValue().get( 0 );
                    }
                    if (entry.getKey().equals( "oid" )) {
                        oid = entry.getValue().get( 0 );
                    }
                    if (entry.getKey().equals( "title" )) {
                        title = entry.getValue().get( 0 );
                    }
                }

                if (guid != null) {

                    MarkdownContextHelpItemKey mchik = new MarkdownContextHelpItemKey( guid );
                    mchik.setLang( language );
                    if (oid != null) {
                        mchik.setOid( oid );
                    } 
                    if (profile != null) {
                        mchik.setProfile( profile );
                    }

                    MarkdownContextHelpItem mchi = new MarkdownContextHelpItem( path );
                    if (title != null) {
                        mchi.setTitle( title );
                    }
                    result.put( mchik, mchi );
                }
            }
        }
        
        return result;
    }

    public String renderMarkdownFile(Path markdownPath) {

        String renderedNode = null;
        try {
            String content = new String( Files.readAllBytes( Paths.get( markdownPath.toUri() ) ) );

            Node mdNode = parser.parse( content );

            renderedNode = htmlRenderer.render( mdNode );
        } catch (IOException e) {
            LOG.error( "Impossible to open ressource from class path.", e );
            throw new RuntimeException( e );
        }

        return renderedNode;

    }

    public String getContextHelpPath() {
        return contextHelpPath;
    }

    public void setContextHelpPath(String contextHelpPath) {
        this.contextHelpPath = contextHelpPath;
    }

    /**
     * Default language (ISO 639-1)
     * 
     * @return
     */
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * @param language (ISO 639-1)
     */
    public void setDefaultLanguage(String language) {
        this.defaultLanguage = language;
    }
    
    

}
