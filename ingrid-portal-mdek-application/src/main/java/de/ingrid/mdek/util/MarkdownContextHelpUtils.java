package de.ingrid.mdek.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
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

    private static final String DEFAULT_CONTEXT_HELP_PATH = "context_help/";

    private final static Logger LOG = Logger.getLogger( MarkdownContextHelpUtils.class );

    private String contextHelpPath = DEFAULT_CONTEXT_HELP_PATH;
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
        Set<Extension> extensions = Collections.singleton( YamlFrontMatterExtension.create() );
        parser = Parser.builder().extensions( extensions ).build();
        htmlRenderer = HtmlRenderer.builder().build();
    }

    /**
     * Returns a Map with rendered HTML from markdown files. Mardown files must
     * contain front matter meta data like this:
     * 
     * <pre>
     * ---
     * # ID des GUI Elements
     * guid: 3000
     * # ID der Objektklasse
     * oid: 1
     * # title, used as windows title
     * title: My Title
     * ---
     * </pre>
     * 
     * @return
     */
    public Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> getAvailableMarkdownHelpFiles() {

        Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> result = new HashMap<MarkdownContextHelpItemKey, MarkdownContextHelpItem>();

        try (Stream<Path> files = Files.list( Paths.get( getClass().getClassLoader().getResource( contextHelpPath ).toURI() ) )) {
            List<Path> list = files.filter( Files::isRegularFile ).collect( Collectors.toList() );

            for (Path path : list) {

                String content = new String( Files.readAllBytes( Paths.get( path.toUri() ) ) );

                Node mdNode = parser.parse( content );

                YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
                mdNode.accept( visitor );

                Map<String, List<String>> data = visitor.getData();

                String guid = null;
                String oid = null;
                String lang = null;
                String title = null;

                for (Map.Entry<String, List<String>> entry : data.entrySet()) {
                    if (entry.getKey().equals( "guid" )) {
                        guid = entry.getValue().get( 0 );
                    }
                    if (entry.getKey().equals( "oid" )) {
                        oid = entry.getValue().get( 0 );
                    }
                    if (entry.getKey().equals( "lang" )) {
                        lang = entry.getValue().get( 0 );
                    }
                    if (entry.getKey().equals( "title" )) {
                        title = entry.getValue().get( 0 );
                    }
                }

                if (guid != null) {

                    MarkdownContextHelpItemKey mchik = new MarkdownContextHelpItemKey( guid );

                    if (oid != null) {
                        mchik.setOid( oid );
                    } else if (lang != null) {
                        mchik.setLang( lang );
                    }

                    MarkdownContextHelpItem mchi = new MarkdownContextHelpItem( path );
                    if (title != null) {
                        mchi.setTitle( title );
                    }
                    result.put( mchik, mchi );
                }
            }

        } catch (URISyntaxException e) {
            LOG.error( "Impossible to get ressource from class path.", e );
            throw new RuntimeException( e );
        } catch (IOException e) {
            LOG.error( "Impossible to open ressource from class path.", e );
            throw new RuntimeException( e );
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

}
