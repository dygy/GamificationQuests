data class TextRange(val start: Int, val endExclusive: Int)
public fun main (){
    findMarkerIgnoringSpace("Text [1299bba / 0 0 0 0 1] from David ","[1299 bba / 0 0 0 0 1]")
}
//прошу простить за вид кода, это моя первая программа на Kotlin
//я понимаю, что было бы рациональнее в некоторых моментах сделать иначе.
// но не совсем еще уверен, как это рботает тут.
fun findMarkerIgnoringSpace(text: String, marker: String): TextRange?{
    //объявляю переменную для регулярного выражения
    var regex = marker
    // убираю из строки пробельные символы
    val normalString= text.replace(" ","")
    //убираю из регулярного выражения пробельные символы
    regex= regex.replace(" ","")
    //меняю в регулярном выражение символы, которые будут конфликтовать с функционалом
    // регулярных выражений, добавляя перед ними \\, что есть символ обращения к конкретному символу
    regex = regex.replace("/","\\/")
    regex =regex.replace("[","\\[")
    regex =regex.replace("]","\\]")
    println(regex.toRegex())
    println(normalString)
    // получаем рендж первого слева нахождения.
    // В задании написано вернуть любой, я решил брать первый
    val thisRange =  regex.toRegex(RegexOption.MULTILINE).find(normalString)?.range
    println(thisRange?.start)
    //Если наш range стал null, то возвращаем null (с формулировкой return if помогла IDEA)
    return if (thisRange==null) {
        null
    }
    //В противном случае создаем экзмпляр класса TextRange, который был объявлен в задании.
    else{
        // засовываем в myRange начало и конец thisRange
        val myRange = TextRange(thisRange.start+1,thisRange.endInclusive+1)
        myRange
    }
}
