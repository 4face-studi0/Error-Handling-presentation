import kotlin.text.RegexOption.IGNORE_CASE



/*

## DEFAULT BEHAVIOUR:

As default, we should expect that a function from general Kotlin code can throw an exception, unless differently
specified by its name.
We should not care about it and thus we should not plan a fallback strategy, unless we can totally recover from the
error and take an action that will allow us to complete our process successfully.

Let's focus on the first case:
Thinking about a login process, it will start from the user interaction and it will finish into a feedback for the user.
The user insert its email address, if it's not valid, we cannot complete the login, so we should not try to deal with
the exception, we should just propagate it back to the UI ( ViewModel probably ).
If we try to deal with it, for example returning a null, we're be in a case of information erasure: the user will just
see that the login failed, but doesn't know how ( let alone how tough it will be to debug it! )

Here we should use `domain()` from the following utils.

## Soft fail:
Let consider a scenario where we can login on different services, if an plain username is provide, we use "pm.me" as
default.
In this case it makes sense to use `domainOrDefault`, but its name should still be `-orDefault`, because for the login
is not required the domain, but it might in other places, where we would use `domain`.
So we should focus on the smaller scope of the function, it should not reflects nor care about our business rules, it
should be independently deployable.

###### I also put `-orNull` for cover all the cases, but it would be very rare for it to be needed

## Hard fail:
There are some cases where we are in an exceptional state, but we still wanna continue the process, without throwing an
exception; here are 2 examples:
1) DOH is a good example, API throws an exception, but we still can retry with another DNS
  In this case we can catch the exception from our general Kotlin code ( business logic ), but it would not be great to
  just use a `try / catch (t: Throwable)` because we might be offline or we just requested for an email that does not
  exist anymore, so it would be worthless to try twice, but we should catch only some types of exception and, if
  possible, the layer below ( in this case the API ) should give as much detailed information about the error
2) We want to upload a set of attachments
  In that case it would be nice to have a custom error, ideally an enum or a sealed class, so it will return a list of
  `UploadAttachment.Result`

In any case, the error handling must be as closest as possible to the presentation layer ( UI )

As general rule, we should never ignore an exception:
1) In the first case we have a default value and that is enough, it is an exception technically speaking, it is at the
  scope of the function ( String.domain() ), it is for some low level components ( e.g. backend is expecting an explicit
  domain ), but it's not an exception for our presentation layer ( user input ), since it's expected to be able to login
  providing only the username, rather the full email address, but also in this case the exception handling should be dealt
  with as close as possible to the presentation layer, like in the ViewModel for example, so low level components will
  receive the right domain, rather than dealing with the absence of it and provide a default value in many places
2) In the second case we're proving a fallback, a B plan, but if required we could keep track of it, and instead of
  returning a `String`, we should return a `StringResult(content: String, usingDoh: Boolean)` or another kind of object
3) In the third case we have the best scenario, as we are not only propagating the error, but we're also proving
  more information with a custom object ( ofc that was just an example, we could have a better fine grained
  handling )

Anyways, never catch exceptions when not strictly required! It's always easier to introduce an error handling later,
than remove it!

 */

/**
 * @return domain for receiver [String] email. if receiver is not en email
 * Example `protonmail.com` from `davide@protonmail.com.domain()`
 *
 * @throws IllegalArgumentException if receiver [String] is not an email
 * @see String.isEmail
 */
fun String.domain(): String {
    require(isEmail()) { "'$this' is not an email" }
    return substringAfter('@')
}



/**
 * @return domain for receiver [String] email, or `null` if receiver is not en email
 * @see String.isEmail
 * Example `null` from `davide.domain()`
 */
fun String.domainOrNull(): String? =
    if (isEmail()) domain()
    else null



/**
 * @return domain for receiver [String] email, or [default] if receiver is not en email
 * @see String.isEmail
 * Example `pm.me` from `davide.domain("pm.me")`
 */
fun String.domainOrDefault(default: String): String =
    domainOrNull() ?: default



/**
 * @return `true` if receiver [String] is email, `false` otherwise
 * ( No error handling required )
 */
fun String.isEmail() =
    """.+@.+\..+""".toRegex(IGNORE_CASE).matches(this)
