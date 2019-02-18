package com.vishnus1224.simplyrss.feedlibrary.addnewfeed

import java.util.regex.Pattern

internal sealed class AddFeedValidationResult
internal object TitleInvalid : AddFeedValidationResult()
internal object UrlInvalid : AddFeedValidationResult()
internal object DescriptionInvalid : AddFeedValidationResult()
internal object InputIsValid : AddFeedValidationResult()

internal fun validateAddFeedInput(
    title: String,
    url: String,
    description: String
): AddFeedValidationResult {
    return run {
        val isTitleValid = title.trim().isNotEmpty()
        if (isTitleValid.not()) return@run TitleInvalid

        val pattern =
            Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?+%/.\\w]+)?")

        val matcher = pattern.matcher(url)

        val isUrlValid = matcher.matches()
        if (isUrlValid.not()) return@run UrlInvalid

        val isDescriptionValid = description.trim().isNotEmpty()
        if (isDescriptionValid.not()) return@run DescriptionInvalid

        InputIsValid
    }
}