import java.util.*;
import java.util.stream.Collectors;
class KurdishDidYouMean {
    
    // Sample Kurdish dictionary - you can expand this with more words or words you get from your list
    private static final Set<String> KURDISH_DICTIONARY = new HashSet<>(Arrays.asList(
        //(Sorani dialect examples)
        "سڵاو", "چۆنی", "سوپاس", "بەڵێ", "نەخێر", "ماڵ", "ئاو", "نان", "کتێب", "قوتابی",
        "مامۆستا", "دایک", "باوک", "برا", "خوشک", "هاوڕێ", "ئیشت", "خواردن", "خەوتن", "وتن",
        "بینین", "گوێگرتن", "ڕۆیشتن", "هاتن", "کردن", "زانین", "فێربوون", "یاری", "کار", "پێکەنین",
        
        // (Kurmanji examples)
        "silav", "çoni", "spas", "erê", "na", "mal", "av", "nan", "pirtûk", "xwendekar",
        "mamosta", "dayik", "bav", "bira", "xwişk", "heval", "kar", "xwarin", "razân", "gotin",
        "dîtin", "guhdarî", "çûn", "hatin", "kirin", "zanîn", "hînbûn", "lîstin", "pêkenîn"
    ));
    
    /**
     * Main "Did You Mean" function that suggests corrections for a misspelled word
     * @param input The potentially misspelled word
     * @param maxSuggestions Maximum number of suggestions to return
     * @param threshold Maximum edit distance to consider (lower = stricter matching)
     * @return List of suggested corrections sorted by similarity
     */
    public static List<String> didYouMean(String input, int maxSuggestions, int threshold) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // If the word exists in dictionary, no suggestions needed
        if (KURDISH_DICTIONARY.contains(input)) {
            return new ArrayList<>();
        }
        
        // Calculate similarities and collect suggestions
        List<WordSimilarity> similarities = new ArrayList<>();
        
        for (String dictWord : KURDISH_DICTIONARY) {
            int distance = calculateLevenshteinDistance(input, dictWord);
            
            // Only consider words within the threshold
            if (distance <= threshold) {
                double similarity = calculateSimilarityScore(input, dictWord, distance);
                similarities.add(new WordSimilarity(dictWord, similarity, distance));
            }
        }
        
        // Sort by similarity score (higher is better) and then by edit distance (lower is better)
        return similarities.stream()
                .sorted((a, b) -> {
                    int scoreCompare = Double.compare(b.similarity, a.similarity);
                    if (scoreCompare == 0) {
                        return Integer.compare(a.editDistance, b.editDistance);
                    }
                    return scoreCompare;
                })
                .limit(maxSuggestions)
                .map(ws -> ws.word)
                .collect(Collectors.toList());
    }
    
    /**
     * Overloaded method with default parameters
     */
    public static List<String> didYouMean(String input) {
        return didYouMean(input, 3, 3);
    }
    
    /**
     * Calculate Levenshtein distance between two strings
     * This works well for Kurdish text including Unicode characters
     */
    private static int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        // Initialize base cases
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        // Fill the DP table
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    /**
     * Calculate a similarity score considering multiple factors
     */
    private static double calculateSimilarityScore(String input, String candidate, int editDistance) {
        // Base similarity based on edit distance
        double maxLength = Math.max(input.length(), candidate.length());
        double editSimilarity = (maxLength - editDistance) / maxLength;
        
        // Bonus for length similarity
        double lengthSimilarity = 1.0 - Math.abs(input.length() - candidate.length()) / maxLength;
        
        // Bonus for common prefix/suffix
        double prefixBonus = calculatePrefixSimilarity(input, candidate) * 0.3;
        double suffixBonus = calculateSuffixSimilarity(input, candidate) * 0.2;
        
        // Weighted combination
        return editSimilarity * 0.6 + lengthSimilarity * 0.2 + prefixBonus + suffixBonus;
    }
    
    /**
     * Calculate similarity based on common prefix
     */
    private static double calculatePrefixSimilarity(String s1, String s2) {
        int commonPrefix = 0;
        int minLength = Math.min(s1.length(), s2.length());
        
        for (int i = 0; i < minLength; i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                commonPrefix++;
            } else {
                break;
            }
        }
        
        return (double) commonPrefix / Math.max(s1.length(), s2.length());
    }
    
    /**
     * Calculate similarity based on common suffix
     */
    private static double calculateSuffixSimilarity(String s1, String s2) {
        int commonSuffix = 0;
        int i = s1.length() - 1;
        int j = s2.length() - 1;
        
        while (i >= 0 && j >= 0 && s1.charAt(i) == s2.charAt(j)) {
            commonSuffix++;
            i--;
            j--;
        }
        
        return (double) commonSuffix / Math.max(s1.length(), s2.length());
    }
    
    /**
     * Add words to the dictionary
     */
    public static void addToDictionary(String... words) {
        KURDISH_DICTIONARY.addAll(Arrays.asList(words));
    }
    
    /**
     * Check if a word exists in the dictionary
     */
    public static boolean isInDictionary(String word) {
        return KURDISH_DICTIONARY.contains(word);
    }
    
    /**
     * Helper class to store word similarity information
     */
    private static class WordSimilarity {
        String word;
        double similarity;
        int editDistance;
        
        WordSimilarity(String word, double similarity, int editDistance) {
            this.word = word;
            this.similarity = similarity;
            this.editDistance = editDistance;
        }
    }
    
    // Example usage and testing
    public static void main(String[] args) {
        // Test with various misspelled Kurdish words
        System.out.println("Kurdish 'Did You Mean' Function Demo");
        System.out.println("=====================================");
        
        // Test cases
        String[] testWords = {
            "سڵو",      // misspelled سڵاو
            "چنی",      // misspelled چۆنی  
            "سپاس",     // misspelled سوپاس
            "maal",     // misspelled mal
            "silav",    // correct word
            "pirtuk",   // misspelled pirtûk
            "dayik",    // correct word
            "کتب",      // misspelled کتێب
            "xyz"       // completely wrong
        };
        
        for (String testWord : testWords) {
            System.out.println("\nInput: \"" + testWord + "\"");
            
            if (isInDictionary(testWord)) {
                System.out.println("✓ Word found in dictionary");
            } else {
                List<String> suggestions = didYouMean(testWord);
                if (suggestions.isEmpty()) {
                    System.out.println("✗ No suggestions found");
                } else {
                    System.out.println("Did you mean: " + suggestions);
                }
            }
        }
        
        // Demonstrate adding custom words
        System.out.println("\n\nAdding custom words to dictionary...");
        addToDictionary("کوردستان", "ئەمن", "ئاسمان");
        
        System.out.println("Testing with newly added word:");
        List<String> suggestions = didYouMean("ئاسان"); // should suggest ئاسمان
        System.out.println("Input: \"ئاسان\" -> Suggestions: " + suggestions);
    }
}
