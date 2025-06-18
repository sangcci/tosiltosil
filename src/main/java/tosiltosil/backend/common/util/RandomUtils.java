package tosiltosil.backend.common.util;

import java.security.SecureRandom;

/**
 * 랜덤 유틸
 *
 * @author mosun
 */
public class RandomUtils {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String MIX_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String NUMBER_CHARACTERS = "0123456789";

    /**
     * 숫자, 영문으로 구성된 length 길이의 랜덤 문자열 반환
     *
     * @param length 문자열 길이
     * @param isUppercase 대소문자 여부
     * @return 대소문자 여부에 따라 랜덤한 숫자 + 영문 조합의 문자열
     */
    public static String generateRandomMixString(int length, boolean isUppercase) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(MIX_CHARACTERS.length());
            sb.append(MIX_CHARACTERS.charAt(index));
        }

        return isUppercase ? sb.toString() : sb.toString().toLowerCase();
    }

    /**
     * 숫자로 구성된 length 길이의 랜덤 문자열 반환
     *
     * @param length 문자열 길이
     * @return 랜덤한 숫자로 구성된 문자열
     */
    public static String generateRandomNumberString(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(NUMBER_CHARACTERS.length());
            sb.append(NUMBER_CHARACTERS.charAt(index));
        }

        return sb.toString();
    }
}
