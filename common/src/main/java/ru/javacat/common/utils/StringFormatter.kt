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

fun String.toPrettyNumber(): String{
    println("CURRENT PHONE NUMBER: $this")
    var pretty = this
    if (this.startsWith("+7",true) || this.startsWith("8",true)) {
            val regex = Regex("""[^\d]""")
            val clean = regex.replace(this@toPrettyNumber, "")
            println("clean: $clean")
            val sub = clean.substring(1)

            if (sub.length>=10){
                val new = "+7${sub}"
                pretty = StringBuilder().apply {
                append(new.substring(0,2))
                append(new.substring(2,5))
                append(" ")
                append(new.substring(5, 8))
                append("-")
                append(new.substring(8, 10))
                append("-")
                append(new.substring(10, 12))
            }.toString()
        }
    }
    return pretty
}

fun Int.toPrettyPrice(): String{
    return "${this / 1000}.%03d".format(this % 1000)
}