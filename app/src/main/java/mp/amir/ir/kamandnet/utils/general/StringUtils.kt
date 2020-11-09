package mp.amir.ir.kamandnet.utils.general

fun String.en2Fa(): String {
    return this.replace("0", "۰")
        .replace("1", "۱")
        .replace("2", "۲")
        .replace("3", "۳")
        .replace("4", "۴")
        .replace("5", "۵")
        .replace("6", "۶")
        .replace("7", "۷")
        .replace("8", "۸")
        .replace("9", "۹")
}

fun String.fa2En(): String {
    return replace("۰", "0")
        .replace("۱", "1")
        .replace("۲", "2")
        .replace("۳", "3")
        .replace("۴", "4")
        .replace("۵", "5")
        .replace("۶", "6")
        .replace("۷", "7")
        .replace("۸", "8")
        .replace("۹", "9")
        //iphone numeric
        .replace("٠", "0")
        .replace("١", "1")
        .replace("٢", "2")
        .replace("٣", "3")
        .replace("٤", "4")
        .replace("٥", "5")
        .replace("٦", "6")
        .replace("٧", "7")
        .replace("٨", "8")
        .replace("٩", "9")
}

fun String.fixPersianChars(): String {
    return replace("ﮎ", "ک")
        .replace("ﮏ", "ک")
        .replace("ﮐ", "ک")
        .replace("ﮑ", "ک")
        .replace("ك", "ک")
        .replace("ي", "ی")
        .replace(" ", " ")
        .replace("‌", " ")
        .replace("ھ", "ه")//.replace("ئ", "ی");
}

fun String.clearString(): String {
    return this.trim().fixPersianChars().fa2En()
}