package de.ingrid.mdek.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
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
 * Scans the context help directory (defaults to "context_help") for markdown files. Returns HTML rendered representation.
 * 
 * @author jm
 *
 */
public class MarkdownContextHelpUtils {

    private static final String DEFAULT_CONTEXT_HELP_PATH = "context_help/";

    private static final Set<Extension> EXTENSIONS = Collections.singleton( YamlFrontMatterExtension.create() );
    private static final Parser PARSER = Parser.builder().extensions( EXTENSIONS ).build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().build();
    private final static Logger LOG = Logger.getLogger( MarkdownContextHelpUtils.class );
    
    
    private String contextHelpPath = null;
    
    public MarkdownContextHelpUtils() {
        this.contextHelpPath = DEFAULT_CONTEXT_HELP_PATH;
    }

    public MarkdownContextHelpUtils(String contextHelpPath) {
        this.contextHelpPath = contextHelpPath;
    }

    /**
     * Returns a Map with rendered HTML from markdown files. Mardown files must contain front matter meta data like this:
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
    public Map<MarkdownContextHelpItem, String> buildAvailableMarkdownHelp() {

        Map<MarkdownContextHelpItem, String> result = new HashMap<MarkdownContextHelpItem, String>();

        try (Stream<Path> files = Files.list( Paths.get( getClass().getClassLoader().getResource( contextHelpPath ).toURI() ) )) {
            List<InputStream> list = files.filter( Files::isRegularFile ).map( path -> {
                try {
                    return Files.newInputStream( path );
                } catch (IOException ex) {
                    throw new UncheckedIOException( ex );
                }
            } ).collect( Collectors.toList() );

            for (InputStream stream : list) {

                try (BufferedReader buffer = new BufferedReader( new InputStreamReader( stream ) )) {
                    String content = buffer.lines().collect( Collectors.joining( "\n" ) );
                    Node mdNode = PARSER.parse( content );

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

                        String renderedNode = HTML_RENDERER.render(mdNode);

                        MarkdownContextHelpItem item = new MarkdownContextHelpItem( guid );

                        if (oid != null) {
                            item.setOid( oid );
                        } else if (title != null) {
                            item.setTitle( title );
                        }
                        result.put( item, renderedNode );
                    }
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

}
