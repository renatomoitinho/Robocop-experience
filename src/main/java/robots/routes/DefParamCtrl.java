package robots.routes;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 06/03/14 22:53
 */
public class DefParamCtrl implements ParamCtrl {

    private final List<String> parameters = new ArrayList<>();
    private final Pattern pattern;

    public static DefParamCtrl of(String originalPattern){
        return new DefParamCtrl(originalPattern);
    }

    public DefParamCtrl(String originalPattern, Map<String, String> parameterPatterns) {
        this.pattern = compilePattern(originalPattern,parameterPatterns);
    }

    public DefParamCtrl(String originalPattern) {
        this(originalPattern, Collections.<String, String>emptyMap());
    }

    private Pattern compilePattern(String originalPattern, Map<String, String> parameterPatterns) {
        Map<String, String> parameters = new HashMap<String, String>(parameterPatterns);
        Matcher matcher = Pattern.compile("\\{((?=[^\\{]+?[\\{])[^\\}]+?\\}|[^\\}]+?)\\}").matcher(originalPattern);
        while (matcher.find()) {
            String value = matcher.group(1);
            String defaultPattern = value.matches("^[^:]+\\*$")? ".*" : value.indexOf(":") >= 0 ? value.replaceAll("^[^\\:]+?:", "") : "[^/]*";
            if (!parameters.containsKey(value)) {
                parameters.put(value, defaultPattern);
            }
            this.parameters.add(value.replaceAll("(\\:.*|\\*)$", ""));
        }
        String patternUri = originalPattern;
        patternUri = patternUri.replaceAll("/\\*", "/.*");
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            patternUri = patternUri.replace("{" + parameter.getKey() + "}", "(" + parameter.getValue() + ")");
        }

        return Pattern.compile(patternUri);
    }
    /**
     * wether the uri matches this uri
     */
    @Override
    public boolean matches(String uri) {
        return pattern.matcher(uri).matches();
    }

    @Override
    public void fillIntoRequest(String uri, Map<String, Object> request) {
        Matcher m = pattern.matcher(uri);
        m.matches();
        for (int i = 1; i <= m.groupCount(); i++) {
            String name = parameters.get(i - 1);
            try {
                request.put(name, URLDecoder.decode(m.group(i), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                System.err.println("Error when decoding url parameters");
            }
        }
    }
}
