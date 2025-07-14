package tosiltosil.backend.common.domain.order;

public class LexoRank {
    
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();
    private static final String MIN_CHAR = "0";
    private static final String MAX_CHAR = "z";
    private static final String INITIAL_RANK = "n";
    
    public static String getInitialRank() {
        return INITIAL_RANK;
    }
    
    public static String getRankBetween(final String prevRank, final String nextRank) {
        if (prevRank == null && nextRank == null) {
            return INITIAL_RANK;
        }
        
        if (prevRank == null) {
            return getRankBefore(nextRank);
        }
        
        if (nextRank == null) {
            return getRankAfter(prevRank);
        }
        
        return getRankBetweenStrings(prevRank, nextRank);
    }
    
    public static String getRankBefore(final String rank) {
        if (rank == null || rank.isEmpty()) {
            return INITIAL_RANK;
        }
        
        return getRankBetweenStrings(null, rank);
    }
    
    public static String getRankAfter(final String rank) {
        if (rank == null || rank.isEmpty()) {
            return INITIAL_RANK;
        }
        
        return getRankBetweenStrings(rank, null);
    }
    
    private static String getRankBetweenStrings(final String prevRank, final String nextRank) {
        if (prevRank == null) {
            return decrementRank(nextRank);
        }
        
        if (nextRank == null) {
            return incrementRank(prevRank);
        }
        
        int minLength = Math.min(prevRank.length(), nextRank.length());
        int diffIndex = -1;
        
        for (int i = 0; i < minLength; i++) {
            if (prevRank.charAt(i) != nextRank.charAt(i)) {
                diffIndex = i;
                break;
            }
        }
        
        if (diffIndex == -1) {
            if (prevRank.length() < nextRank.length()) {
                return prevRank + getMidChar(MIN_CHAR, String.valueOf(nextRank.charAt(prevRank.length())));
            } else if (prevRank.length() > nextRank.length()) {
                return nextRank + getMidChar(String.valueOf(prevRank.charAt(nextRank.length())), MAX_CHAR);
            } else {
                return prevRank + getMidChar(MIN_CHAR, MAX_CHAR);
            }
        }
        
        char prevChar = prevRank.charAt(diffIndex);
        char nextChar = nextRank.charAt(diffIndex);
        
        if (getCharIndex(nextChar) - getCharIndex(prevChar) > 1) {
            String midChar = getMidChar(String.valueOf(prevChar), String.valueOf(nextChar));
            return prevRank.substring(0, diffIndex) + midChar;
        }
        
        return prevRank.substring(0, diffIndex + 1) + getMidChar(MIN_CHAR, MAX_CHAR);
    }
    
    private static String incrementRank(final String rank) {
        if (rank.isEmpty()) {
            return INITIAL_RANK;
        }
        
        char lastChar = rank.charAt(rank.length() - 1);
        if (lastChar == MAX_CHAR.charAt(0)) {
            return rank + getMidChar(MIN_CHAR, MAX_CHAR);
        }
        
        int nextIndex = getCharIndex(lastChar) + 1;
        return rank.substring(0, rank.length() - 1) + ALPHABET.charAt(nextIndex);
    }
    
    private static String decrementRank(final String rank) {
        if (rank.isEmpty()) {
            return INITIAL_RANK;
        }
        
        char firstChar = rank.charAt(0);
        if (firstChar == MIN_CHAR.charAt(0)) {
            return getMidChar(MIN_CHAR, String.valueOf(firstChar)) + rank.substring(1);
        }
        
        int prevIndex = getCharIndex(firstChar) - 1;
        return ALPHABET.charAt(prevIndex) + rank.substring(1);
    }
    
    private static String getMidChar(final String prev, final String next) {
        int prevIndex = getCharIndex(prev.charAt(0));
        int nextIndex = getCharIndex(next.charAt(0));
        int midIndex = (prevIndex + nextIndex) / 2;
        return String.valueOf(ALPHABET.charAt(midIndex));
    }
    
    private static int getCharIndex(char c) {
        return ALPHABET.indexOf(c);
    }
}