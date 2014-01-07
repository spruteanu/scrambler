package org.prismus.scrambler.builder;

import org.prismus.scrambler.property.Util;

import java.util.regex.Pattern;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class RegexPredicate<V> implements ValuePredicate<String> {
    private Pattern pattern;

    @Override
    public boolean apply(String value) {
        return pattern.matcher(value).matches();
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public void setPattern(String wildcardPattern) {
        this.pattern = Pattern.compile(Util.replaceWildcards(wildcardPattern), Pattern.CASE_INSENSITIVE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegexPredicate that = (RegexPredicate) o;
        return pattern.pattern().equals(that.pattern.pattern());
    }

    @Override
    public int hashCode() {
        return pattern.pattern().hashCode();
    }
}
