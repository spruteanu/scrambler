package org.prismus.scrambler.builder;

import org.prismus.scrambler.property.Util;

import java.util.regex.Pattern;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class PropertyPredicate implements ValuePredicate<String> {
    private String wildcardPattern;
    private Pattern pattern;

    public PropertyPredicate() {
    }

    public PropertyPredicate(String wildcardPattern) {
        setPattern(wildcardPattern);
    }

    @Override
    public boolean apply(String value) {
        return (pattern != null && pattern.matcher(value).matches()) || wildcardPattern.equalsIgnoreCase(value);
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
        if (pattern != null) {
            wildcardPattern = pattern.pattern();
        }
    }

    public PropertyPredicate withPattern(Pattern pattern) {
        setPattern(pattern);
        return this;
    }

    public void setPattern(String wildcardPattern) {
        this.wildcardPattern = wildcardPattern;
        final String replacedWildcards = Util.replaceWildcards(wildcardPattern);
        if (replacedWildcards.charAt(0) == '^') {
            this.pattern = Pattern.compile(replacedWildcards, Pattern.CASE_INSENSITIVE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PropertyPredicate that = (PropertyPredicate) o;
        return wildcardPattern.equalsIgnoreCase(that.wildcardPattern);
    }

    @Override
    public int hashCode() {
        return wildcardPattern.toLowerCase().hashCode();
    }

    public static PropertyPredicate of(String wildcardPattern) {
        return new PropertyPredicate(wildcardPattern);
    }

}
