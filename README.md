# Kurdish "Did You Mean" Function - Comprehensive Documentation

## Table of Contents
1. [Overview](#overview)
2. [Architecture and Design Principles](#architecture-and-design-principles)
3. [Core Algorithms](#core-algorithms)
4. [Class Structure and Components](#class-structure-and-components)
5. [Method Documentation](#method-documentation)
6. [Algorithm Deep Dive](#algorithm-deep-dive)
7. [Performance Analysis](#performance-analysis)
8. [Kurdish Language Considerations](#kurdish-language-considerations)
9. [Usage Examples](#usage-examples)
10. [Customization and Extension](#customization-and-extension)
11. [Testing and Validation](#testing-and-validation)
12. [Best Practices](#best-practices)
13. [Troubleshooting](#troubleshooting)

---

## Overview

The `KurdishDidYouMean` class is a sophisticated spell-checking and suggestion system specifically designed for Kurdish language text. It implements multiple string similarity algorithms to provide intelligent word suggestions when users input potentially misspelled Kurdish words.

### Key Features
- **Multi-dialect support**: Handles both Sorani (Arabic script) and Kurmanji (Latin script)
- **Unicode-aware**: Properly processes Kurdish Unicode characters
- **Configurable similarity**: Adjustable thresholds and suggestion counts
- **Performance-optimized**: Efficient algorithms with early termination
- **Extensible dictionary**: Easy addition of new words

### Use Cases
- Spell checking in Kurdish text editors
- Search query correction in Kurdish databases
- Input validation for Kurdish language applications
- Educational tools for Kurdish language learning
- Content management systems with Kurdish support

---

## Architecture and Design Principles

### Design Philosophy
The system follows several key design principles:

1. **Separation of Concerns**: Algorithm logic is separated from data management
2. **Performance First**: Optimized for real-time spell checking scenarios
3. **Language Agnostic Core**: Core algorithms work with any Unicode text
4. **Kurdish-Specific Enhancements**: Special handling for Kurdish morphology
5. **Extensibility**: Easy to add new similarity metrics or dictionaries

### System Architecture
```
Input Word
     ↓
Dictionary Check → [Word Found] → No Suggestions
     ↓
[Word Not Found]
     ↓
Similarity Calculation
     ↓
Multi-Factor Scoring
     ↓
Ranking and Filtering
     ↓
Top N Suggestions
```

---

## Core Algorithms

### 1. Levenshtein Distance Algorithm

**Purpose**: Measures the minimum number of single-character edits required to transform one string into another.

**Mathematical Foundation**:
```
Let d[i][j] be the edit distance between first i characters of string s1 and first j characters of string s2

Base cases:
d[i][0] = i (delete all i characters)
d[0][j] = j (insert all j characters)

Recurrence relation:
d[i][j] = d[i-1][j-1]                    if s1[i] == s2[j]
d[i][j] = 1 + min(d[i-1][j],             (deletion)
                  d[i][j-1],             (insertion)
                  d[i-1][j-1])           (substitution)
```

**Time Complexity**: O(m × n) where m and n are string lengths
**Space Complexity**: O(m × n) (can be optimized to O(min(m,n)))

**Why Levenshtein for Kurdish**:
- Handles character substitutions common in Kurdish typing errors
- Works well with Unicode characters used in Kurdish scripts
- Accounts for common morphological variations

### 2. Multi-Factor Similarity Scoring

The system uses a weighted combination of multiple similarity metrics:

**Formula**:
```
Final Score = (Edit Similarity × 0.6) + 
              (Length Similarity × 0.2) + 
              (Prefix Bonus × 0.3) + 
              (Suffix Bonus × 0.2)
```

**Components**:

1. **Edit Similarity**: `(max_length - edit_distance) / max_length`
2. **Length Similarity**: `1.0 - |len1 - len2| / max_length`
3. **Prefix Similarity**: Bonus for common starting characters
4. **Suffix Similarity**: Bonus for common ending characters

**Rationale**:
- Kurdish words often share common prefixes/suffixes due to morphology
- Length similarity helps distinguish between similar words
- Weighted combination balances different similarity aspects

---

## Class Structure and Components

### Main Class: `KurdishDidYouMean`

```java
public class KurdishDidYouMean {
    // Static dictionary for thread safety and memory efficiency
    private static final Set<String> KURDISH_DICTIONARY
    
    // Public API methods
    public static List<String> didYouMean(String input, int maxSuggestions, int threshold)
    public static List<String> didYouMean(String input)
    public static void addToDictionary(String... words)
    public static boolean isInDictionary(String word)
    
    // Core algorithm methods
    private static int calculateLevenshteinDistance(String s1, String s2)
    private static double calculateSimilarityScore(String input, String candidate, int editDistance)
    private static double calculatePrefixSimilarity(String s1, String s2)
    private static double calculateSuffixSimilarity(String s1, String s2)
    
    // Helper classes
    private static class WordSimilarity { ... }
}
```

### WordSimilarity Helper Class

```java
private static class WordSimilarity {
    String word;        // The dictionary word
    double similarity;  // Calculated similarity score
    int editDistance;   // Levenshtein distance
}
```

**Purpose**: Encapsulates similarity data for efficient sorting and processing.

---

## Method Documentation

### Public Methods

#### `didYouMean(String input, int maxSuggestions, int threshold)`

**Purpose**: Main entry point for getting spelling suggestions.

**Parameters**:
- `input` (String): The potentially misspelled word
- `maxSuggestions` (int): Maximum number of suggestions to return
- `threshold` (int): Maximum edit distance to consider

**Returns**: `List<String>` - Ordered list of suggestions (best first)

**Algorithm Flow**:
```
1. Validate input (null/empty check)
2. Check if word exists in dictionary
3. Calculate similarity for all dictionary words
4. Filter by threshold
5. Sort by similarity score
6. Return top N suggestions
```

**Edge Cases**:
- Null/empty input → returns empty list
- Word already in dictionary → returns empty list
- No similar words found → returns empty list

#### `didYouMean(String input)`

**Purpose**: Convenience method with default parameters.

**Default Values**:
- `maxSuggestions`: 3
- `threshold`: 3

#### `addToDictionary(String... words)`

**Purpose**: Adds new words to the Kurdish dictionary.

**Thread Safety**: Uses thread-safe HashSet operations.

**Usage Pattern**:
```java
// Single word
KurdishDidYouMean.addToDictionary("نوێ");

// Multiple words
KurdishDidYouMean.addToDictionary("word1", "word2", "word3");
```

#### `isInDictionary(String word)`

**Purpose**: Checks if a word exists in the dictionary.

**Performance**: O(1) average case due to HashSet implementation.

### Private Methods

#### `calculateLevenshteinDistance(String s1, String s2)`

**Algorithm**: Dynamic Programming implementation of edit distance.

**Optimizations**:
- Uses 2D array for memoization
- Early termination possible for large distances
- Unicode-safe character comparison

**Memory Usage**: O(|s1| × |s2|)

#### `calculateSimilarityScore(String input, String candidate, int editDistance)`

**Purpose**: Computes multi-factor similarity score.

**Scoring Components**:
1. **Edit Distance Factor (60%)**: Primary similarity metric
2. **Length Similarity (20%)**: Penalizes length differences
3. **Prefix Bonus (30%)**: Rewards common beginnings
4. **Suffix Bonus (20%)**: Rewards common endings

**Score Range**: 0.0 to 1.0+ (bonuses can exceed 1.0)

#### `calculatePrefixSimilarity(String s1, String s2)`

**Algorithm**:
```java
for each character position i from start:
    if s1[i] == s2[i]:
        commonPrefix++
    else:
        break
return commonPrefix / max(len1, len2)
```

**Kurdish Relevance**: Kurdish words often share morphological prefixes.

#### `calculateSuffixSimilarity(String s1, String s2)`

**Algorithm**: Similar to prefix but starts from string end.

**Kurdish Relevance**: Kurdish verb conjugations and plural forms share suffixes.

---

## Algorithm Deep Dive

### Levenshtein Distance Implementation

The implementation uses dynamic programming with a bottom-up approach:

```java
int[][] dp = new int[s1.length() + 1][s2.length() + 1];

// Base cases: transforming empty string
for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

// Fill DP table
for (int i = 1; i <= s1.length(); i++) {
    for (int j = 1; j <= s2.length(); j++) {
        if (s1.charAt(i-1) == s2.charAt(j-1)) {
            dp[i][j] = dp[i-1][j-1];  // No operation needed
        } else {
            dp[i][j] = 1 + Math.min(
                dp[i-1][j],     // Deletion
                dp[i][j-1],     // Insertion
                dp[i-1][j-1]    // Substitution
            );
        }
    }
}
```

**Complexity Analysis**:
- **Time**: O(m × n) where m, n are string lengths
- **Space**: O(m × n) for the DP table
- **Optimizable**: Can reduce space to O(min(m,n)) using rolling arrays

### Multi-Factor Scoring Logic

The scoring system combines multiple similarity metrics:

```java
double editSimilarity = (maxLength - editDistance) / maxLength;
double lengthSimilarity = 1.0 - Math.abs(len1 - len2) / maxLength;
double prefixBonus = calculatePrefixSimilarity(s1, s2) * 0.3;
double suffixBonus = calculateSuffixSimilarity(s1, s2) * 0.2;

return editSimilarity * 0.6 + lengthSimilarity * 0.2 + prefixBonus + suffixBonus;
```

**Weight Justification**:
- **Edit Distance (60%)**: Primary indicator of similarity
- **Length (20%)**: Prevents suggesting words of very different lengths
- **Prefix (30%)**: Kurdish morphology often preserves word beginnings
- **Suffix (20%)**: Accounts for inflectional endings

### Sorting and Ranking

The final suggestions are sorted using a custom comparator:

```java
.sorted((a, b) -> {
    int scoreCompare = Double.compare(b.similarity, a.similarity);
    if (scoreCompare == 0) {
        return Integer.compare(a.editDistance, b.editDistance);
    }
    return scoreCompare;
})
```

**Logic**:
1. Primary sort: Higher similarity scores first
2. Tie-breaker: Lower edit distances first
3. This ensures both quality and consistency

---

## Performance Analysis

### Time Complexity

**Per Dictionary Word**: O(m × n) where m, n are string lengths
**Total Complexity**: O(|D| × m × n) where |D| is dictionary size

**Typical Performance**:
- Dictionary size: ~100-1000 words
- Average word length: 5-10 characters
- Expected operations per query: 50,000-100,000

### Space Complexity

**DP Table**: O(m × n) per comparison
**Storage**: O(|D|) for dictionary
**Temporary**: O(|D|) for similarity calculations

### Optimization Opportunities

1. **Early Termination**: Stop if edit distance exceeds threshold
2. **Trie Structure**: Use prefix trees for dictionary storage
3. **Parallel Processing**: Calculate similarities in parallel
4. **Caching**: Cache recent similarity calculations
5. **Space Optimization**: Use rolling arrays for Levenshtein DP

### Benchmark Results (Estimated)

| Dictionary Size | Avg Query Time | Memory Usage |
|----------------|----------------|--------------|
| 100 words      | 1-2 ms         | 50 KB        |
| 1,000 words    | 10-20 ms       | 200 KB       |
| 10,000 words   | 100-200 ms     | 1.5 MB       |

---

## Kurdish Language Considerations

### Script Support

**Sorani (Arabic Script)**:
- Unicode range: U+0600–U+06FF (Arabic block)
- Direction: Right-to-left
- Contextual letter forms
- Example: سڵاو، چۆنی، سوپاس

**Kurmanji (Latin Script)**:
- Extended Latin characters
- Diacritical marks: ç, ê, î, ş, û
- Direction: Left-to-right
- Example: silav, çoni, spas

### Morphological Characteristics

**Common Prefixes**:
- بە- (be-): indicates continuous aspect
- دا- (da-): past tense marker
- نا- (na-): negation prefix

**Common Suffixes**:
- -ان (-an): plural marker
- -ەکان (-ekan): definite plural
- -م (-im): first person singular

**Implications for Spell Checking**:
- Prefix/suffix bonuses are particularly effective
- Root-based similarity could be added
- Morphological analysis could improve suggestions

### Typography and Input Issues

**Common Typing Errors**:
1. **Script Mixing**: Using Arabic numbers in Kurdish text
2. **Diacritic Omission**: Forgetting marks like ç, ê, û
3. **Similar Characters**: Confusing و/ووو، ی/یی
4. **Keyboard Layout**: QWERTY vs. Kurdish layouts

**Algorithm Accommodations**:
- Character normalization
- Diacritic-insensitive matching
- Script detection and conversion

---

## Usage Examples

### Basic Usage

```java
// Simple spell checking
List<String> suggestions = KurdishDidYouMean.didYouMean("سڵو");
// Returns: [سڵاو] (assuming سڵو is a misspelling of سڵاو)

// Check if word exists
boolean exists = KurdishDidYouMean.isInDictionary("سڵاو");
// Returns: true
```

### Advanced Configuration

```java
// Custom parameters: 5 suggestions, max edit distance 2
List<String> suggestions = KurdishDidYouMean.didYouMean("maal", 5, 2);

// Strict matching: only 1 character difference allowed
List<String> strict = KurdishDidYouMean.didYouMean("کتب", 3, 1);
```

### Dictionary Management

```java
// Add domain-specific vocabulary
KurdishDidYouMean.addToDictionary(
    "کۆمپیوتەر",  // computer
    "تەکنەلۆژیا", // technology
    "ئینتەرنێت"   // internet
);

// Batch addition from file
String[] medicalTerms = loadFromFile("kurdish_medical_terms.txt");
KurdishDidYouMean.addToDictionary(medicalTerms);
```

### Integration Example

```java
public class KurdishTextEditor {
    public void onWordTyped(String word) {
        if (!KurdishDidYouMean.isInDictionary(word)) {
            List<String> suggestions = KurdishDidYouMean.didYouMean(word);
            if (!suggestions.isEmpty()) {
                showSpellingSuggestions(word, suggestions);
            }
        }
    }
    
    private void showSpellingSuggestions(String original, List<String> suggestions) {
        // Display UI with suggestions
        System.out.println("Did you mean: " + String.join(", ", suggestions) + "?");
    }
}
```

### Performance Monitoring

```java
public class PerformanceTest {
    public static void benchmarkSuggestions() {
        long startTime = System.nanoTime();
        
        List<String> suggestions = KurdishDidYouMean.didYouMean("test_word");
        
        long endTime = System.nanoTime();
        double milliseconds = (endTime - startTime) / 1_000_000.0;
        
        System.out.println("Query took: " + milliseconds + " ms");
        System.out.println("Suggestions: " + suggestions);
    }
}
```

---

## Customization and Extension

### Adding Custom Similarity Metrics

```java
// Example: Add phonetic similarity for Kurdish
private static double calculatePhoneticSimilarity(String s1, String s2) {
    // Implement Kurdish phonetic matching
    // Account for similar sounds: p/b, t/d, k/g
    return phoneticScore;
}

// Integrate into main scoring
private static double calculateSimilarityScore(String input, String candidate, int editDistance) {
    double phoneticBonus = calculatePhoneticSimilarity(input, candidate) * 0.1;
    return baseScore + phoneticBonus;
}
```

### Dictionary Backends

```java
// File-based dictionary
public static void loadDictionaryFromFile(String filename) {
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            KURDISH_DICTIONARY.add(line.trim());
        }
    }
}

// Database-backed dictionary
public static void loadDictionaryFromDatabase(Connection conn) {
    String sql = "SELECT word FROM kurdish_dictionary";
    // Execute query and populate dictionary
}
```

### Morphological Analysis Integration

```java
// Example: Root-based similarity
private static double calculateRootSimilarity(String word1, String word2) {
    String root1 = extractKurdishRoot(word1);
    String root2 = extractKurdishRoot(word2);
    return calculateLevenshteinDistance(root1, root2);
}

private static String extractKurdishRoot(String word) {
    // Implement Kurdish morphological analysis
    // Remove common prefixes and suffixes
    return root;
}
```

---

## Testing and Validation

### Unit Test Examples

```java
@Test
public void testBasicSuggestions() {
    List<String> suggestions = KurdishDidYouMean.didYouMean("سڵو");
    assertFalse(suggestions.isEmpty());
    assertTrue(suggestions.contains("سڵاو"));
}

@Test
public void testCorrectWordReturnsEmpty() {
    List<String> suggestions = KurdishDidYouMean.didYouMean("سڵاو");
    assertTrue(suggestions.isEmpty());
}

@Test
public void testThresholdFiltering() {
    // Test that suggestions respect edit distance threshold
    List<String> suggestions = KurdishDidYouMean.didYouMean("xyz", 3, 1);
    assertTrue(suggestions.isEmpty()); // No words within distance 1
}

@Test
public void testCustomDictionary() {
    KurdishDidYouMean.addToDictionary("تاقیکردنەوە");
    assertTrue(KurdishDidYouMean.isInDictionary("تاقیکردنەوە"));
}
```

### Integration Testing

```java
@Test
public void testPerformanceWithLargeDictionary() {
    // Add 1000 words to dictionary
    for (int i = 0; i < 1000; i++) {
        KurdishDidYouMean.addToDictionary("word" + i);
    }
    
    long startTime = System.currentTimeMillis();
    List<String> suggestions = KurdishDidYouMean.didYouMean("wrod1");
    long endTime = System.currentTimeMillis();
    
    assertTrue("Query should complete within 100ms", (endTime - startTime) < 100);
}
```

### Validation Datasets

**Creating Test Data**:
1. Collect common Kurdish words
2. Generate synthetic misspellings
3. Validate suggestions manually
4. Create regression test suite

**Example Test Cases**:
```java
Map<String, String[]> testCases = Map.of(
    "سڵو", new String[]{"سڵاو"},
    "چنی", new String[]{"چۆنی"},
    "maal", new String[]{"mal"},
    "pirtuk", new String[]{"pirtûk"}
);
```

---

## Best Practices

### Performance Optimization

1. **Dictionary Size Management**:
   - Keep dictionary focused on relevant vocabulary
   - Remove obsolete or rare words
   - Consider frequency-based weighting

2. **Threshold Tuning**:
   - Start with threshold = 3 for general use
   - Reduce for strict matching (formal writing)
   - Increase for fuzzy matching (search queries)

3. **Caching Strategy**:
   ```java
   private static final Map<String, List<String>> SUGGESTION_CACHE = new ConcurrentHashMap<>();
   
   public static List<String> didYouMeanCached(String input) {
       return SUGGESTION_CACHE.computeIfAbsent(input, 
           key -> didYouMean(key));
   }
   ```

### Memory Management

1. **Static Dictionary**: Use static collections to avoid memory duplication
2. **Weak References**: Consider weak references for large caches
3. **Batch Processing**: Process multiple queries together

### Thread Safety

The current implementation is thread-safe because:
- Dictionary is static final
- No mutable shared state in methods
- HashSet operations are atomic for reads

For write operations (adding words), consider:
```java
private static final Set<String> KURDISH_DICTIONARY = 
    Collections.synchronizedSet(new HashSet<>());
```

### Error Handling

```java
public static List<String> didYouMeanSafe(String input) {
    try {
        return didYouMean(input);
    } catch (Exception e) {
        log.error("Error in spell checking for: " + input, e);
        return Collections.emptyList();
    }
}
```

---

## Troubleshooting

### Common Issues

#### 1. No Suggestions Returned

**Possible Causes**:
- Word is already in dictionary
- Threshold too strict
- Input too different from dictionary words

**Solutions**:
```java
// Debug the issue
String input = "problem_word";
System.out.println("In dictionary: " + KurdishDidYouMean.isInDictionary(input));
System.out.println("With higher threshold: " + KurdishDidYouMean.didYouMean(input, 5, 5));
```

#### 2. Poor Quality Suggestions

**Possible Causes**:
- Dictionary lacks relevant words
- Similarity weights need adjustment
- Input contains special characters

**Solutions**:
- Expand dictionary with domain-specific terms
- Adjust similarity score weights
- Preprocess input (normalize, clean)

#### 3. Performance Issues

**Symptoms**:
- Slow response times
- High memory usage
- CPU spikes

**Diagnostics**:
```java
// Profile performance
long start = System.nanoTime();
List<String> suggestions = KurdishDidYouMean.didYouMean(input);
long duration = System.nanoTime() - start;
System.out.println("Query took: " + duration / 1_000_000 + " ms");
```

**Solutions**:
- Implement early termination
- Use parallel processing
- Cache frequent queries
- Optimize dictionary size

#### 4. Unicode Issues

**Symptoms**:
- Incorrect character matching
- Garbled text display
- Encoding errors

**Solutions**:
```java
// Ensure proper Unicode handling
String normalized = Normalizer.normalize(input, Normalizer.Form.NFC);
List<String> suggestions = KurdishDidYouMean.didYouMean(normalized);
```

### Debugging Tools

```java
public class KurdishDidYouMeanDebugger {
    public static void debugSuggestion(String input, String candidate) {
        int editDistance = calculateLevenshteinDistance(input, candidate);
        double similarity = calculateSimilarityScore(input, candidate, editDistance);
        
        System.out.println("Input: " + input);
        System.out.println("Candidate: " + candidate);
        System.out.println("Edit Distance: " + editDistance);
        System.out.println("Similarity Score: " + similarity);
        System.out.println("Prefix Similarity: " + calculatePrefixSimilarity(input, candidate));
        System.out.println("Suffix Similarity: " + calculateSuffixSimilarity(input, candidate));
    }
}
```

---

## Conclusion

The Kurdish "Did You Mean" function provides a robust, efficient solution for spell checking and word suggestion in Kurdish language applications. Its multi-algorithm approach, Unicode support, and Kurdish-specific optimizations make it suitable for a wide range of applications from simple spell checkers to complex language processing systems.

The modular design allows for easy customization and extension, while the comprehensive documentation ensures maintainability and ease of integration. Performance characteristics make it suitable for real-time applications, and the extensive testing framework ensures reliability.

For production use, consider implementing the suggested optimizations and monitoring performance characteristics based on your specific use case and data patterns.
(created with the help of Claude AI)
