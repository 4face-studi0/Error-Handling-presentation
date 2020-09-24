import assert4k.*
import org.junit.Test

class StringUtilsTest {

    @Test
    fun `domain works correctly`() {
        assert that "davide@protonmail.com".domain() equals "protonmail.com"
        assert that fails<IllegalArgumentException> { "davide".domain() } with "'davide' is not an email"
    }

    @Test
    fun `domainOrNull works correctly`() {
        assert that "davide@protonmail.com".domainOrNull() equals "protonmail.com"
        assert that "davide".domainOrNull() `is` Null
    }

    @Test
    fun `domainOrDefault works correctly`() {
        assert that "davide@protonmail.com".domainOrDefault("pm.me") equals "protonmail.com"
        assert that "davide".domainOrDefault("pm.me") equals "pm.me"
    }

    @Test
    fun `isEmail works correctly`() {
        assert that "davide@protonmail.com".isEmail()
        assert that fails { assert that "davide@protonmail.com".isEmail().not() }

        assert that "davide".isEmail().not()
        assert that fails { assert that "davide".isEmail() }
    }
}
