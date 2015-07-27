package org.prismus.scrambler.value;

import org.prismus.scrambler.ValuePredicate;

import java.util.regex.Pattern;

/**
 * Property matcher using regular expression. Used when an Instance definition is built to
 * match property by name. Property predicate can be expressed either by wildcard pattern
 * (that is converted into regular expression pattern) or provided by an explicit {@link Pattern}
 * Supported wildcard characters:
 * <li/>*   -> all
 * <li/>?   -> one character
 * Found regular expression characters are ignored (prefixed)
 *
 * @author Serge Pruteanu
 */
public class PropertyPredicate implements ValuePredicate {
    private String wildcardPattern;
    private Pattern pattern;

    public PropertyPredicate() {
    }

    public PropertyPredicate(String wildcardPattern) {
        setPattern(wildcardPattern);
    }

    public PropertyPredicate(Pattern pattern) {
        withPattern(pattern);
    }

    @Override
    public boolean apply(String property, Object value) {
        return (pattern != null && pattern.matcher(property).matches()) || wildcardPattern.equalsIgnoreCase(property);
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

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return wildcardPattern;
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

    public static PropertyPredicate of(Pattern pattern) {
        return new PropertyPredicate(pattern);
    }

}
