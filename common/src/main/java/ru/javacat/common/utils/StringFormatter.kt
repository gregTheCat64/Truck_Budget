package ru.javacat.common.utils

fun String.formatCompanyName(): String {
    return this
        .replace("Ооо", "ООО")
        .replace("Ип", "ИП")
        .trim()
}

fun String.toShortCompanyName(): String {
    return this
        .replace("ООО", "")
        .replace("Ооо", "")
        .replace("ИП", "")
        .replace("Ип", "")
        .replace("\"", "").trim()
}