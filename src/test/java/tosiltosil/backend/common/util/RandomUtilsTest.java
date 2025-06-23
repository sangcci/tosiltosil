package tosiltosil.backend.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RandomUtilsTest {

    @Test
    void 대문자_영문와_숫자_섞어서_6자리_랜덤_문자열_생성() {
        // given
        int length = 6;
        boolean isUppercase = true;

        // when
        String randomStr = RandomUtils.generateRandomMixString(length, isUppercase);

        // then
        assertThat(randomStr.length()).isEqualTo(length);
        assertThat(randomStr).matches("[A-Z0-9]{6}");
    }

    @Test
    void 소문자_영문와_숫자_섞어서_6자리_랜덤_문자열_생성() {
        // given
        int length = 6;
        boolean isUppercase = false;

        // when
        String randomStr = RandomUtils.generateRandomMixString(length, isUppercase);

        // then
        assertThat(randomStr.length()).isEqualTo(length);
        assertThat(randomStr).matches("[a-z0-9]{6}");
    }

    @Test
    void 숫자_구성된_6자리_랜덤_문자열_생성() {
        // given
        int length = 6;

        // when
        String randomStr = RandomUtils.generateRandomNumberString(length);

        // then
        assertThat(randomStr.length()).isEqualTo(length);
        assertThat(randomStr).matches("[0-9]{6}");
    }
}
