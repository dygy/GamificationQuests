import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader
import org.apache.commons.mail.util.MimeMessageParser
import org.jsoup.Jsoup;
import java.io.StringReader
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.util.*
import javax.mail.*
import javax.mail.internet.*
import com.sun.xml.internal.ws.encoding.xml.XMLMessage.createDataSource



fun main (){

    // Recipient's email ID needs to be mentioned.
    val to = "destinationemail@gmail.com"
    // Sender's email ID needs to be mentioned
    val from = "fromemail@gmail.com"
    val username = "manishaspatil"//change accordingly
    val password = "******"//change accordingly

    // Assuming you are sending email through relay.jangosmtp.net
    val host = "relay.jangosmtp.net"
    val props = Properties()
    props["mail.smtp.auth"] = "true"
    props["mail.smtp.starttls.enable"] = "true"
    props["mail.smtp.host"] = host
    props["mail.smtp.port"] = "25"
    // Get the Session object.
    val session = Session.getInstance(props,
        object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })
        val message = MimeMessage(session)
        // Set From: header field of the header.
        message.setFrom(InternetAddress(from))
        message.setText("This is actual message")
        message.setHeader("Content-Transfer-Encoding", "base64");
        // Set To: header field of the header.
        message.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(to)
        )

        // Set Subject: header field
        message.subject = "Testing Subject"

        // Send the actual HTML message, as big as you like
        message.sentDate=  Date()

        val textPart = MimeBodyPart()
        textPart.setContent("plane text", "text/plain")
        // HTML version
        val htmlPart = MimeBodyPart()
        htmlPart.setContent("<h1>This is actual message embedded in HTML tags</h1>", "text/html")
        // Create the Multipart.  Add BodyParts to it.
        val mp = MimeMultipart("alternative")
        mp.addBodyPart(textPart)
        mp.addBodyPart(htmlPart)
        message.setContent(mp)
        println(extractText(message))

}
//Я сильно извиняюсь, но в одну функцию, написать парсер, мне не позволили знания Kotlin
//Их будет две.
private fun extractText(mimeMessage: MimeMessage): String {
    // заготовка для сообщения
    var message = "\n New Email: ["
    // проверяю на нулли, чтобы не спамить лишним
    if (mimeMessage.encoding !== null) {
    //   Кодировка сообщения
        message = "$message\n Language: " +mimeMessage.encoding
    }
    if (mimeMessage.sender !== null) {
        //  Автор сообщения
        message = "$message\n From: " +mimeMessage.sender
    }
    if (mimeMessage.sentDate !== null) {
        //  Дата отправки сообщения
        message = "$message\n Sent Date: " +mimeMessage.sentDate.toString()
    }
    if (mimeMessage.receivedDate !== null) {
    //    Дата получения сообщения
        message = "$message\n Receive Date: " +mimeMessage.receivedDate.toString()
    }
    if (mimeMessage.subject !== null) {
    //  Тема сообщения
        message = "$message\n Subject: " +mimeMessage.subject
    }
    if (mimeMessage.contentLanguage !== null) {
    //  Язык контента в сообщении
        message = "$message\n encoding: " +mimeMessage.contentLanguage
    }
    if (mimeMessage.content is Multipart) {
        val mp = mimeMessage.content as Multipart
        val count = mp.count
        // итерация по e-mail'у
        for (i in 0 until count) {
            message= "$message\n" +extractTextFromMultiPart(mp.getBodyPart(i) as MimeBodyPart)
        }
    }
    //Если тип данных простой текст или html. У меня случились проблемы при формирование мультипарта,
    //программа всегда выкидывала text/plain в второй функции, по этому я попробовал объединить
    //функцоинал, и в целом, учитывая что у меня подгорают дедлайны, я решил,
    //что это удовлетворительно.
    else if (mimeMessage.isMimeType("text/plain")||mimeMessage.isMimeType("text/html")){
        message = "$message\n Text: " + Jsoup.parse(mimeMessage.content.toString()).text()
    }
    if (mimeMessage.description !== null) {
        //  Дополнение к сообщению
        message = "$message\n Message Description: " +mimeMessage.description
    }
    return "$message \n ]"
}
//Вытаскивание частей мультипарта. Соответственно все то,
// что у частей быть не может удалено, а в целом копипаст.
fun extractTextFromMultiPart(mimeBodyPart: MimeBodyPart): String {
    // заготовка для сообщения
    var message = "\n  New Part Of Email: ["
    // проверяю на нулли, чтобы не спамить лишним
    if (mimeBodyPart.encoding !== null) {
        //   Кодировка сообщения
        message = "$message\n  encoding: " +mimeBodyPart.encoding
    }
    if (mimeBodyPart.contentLanguage !== null) {
        //  Язык контента в сообщении
        message = "$message\n  Language: " +mimeBodyPart.contentLanguage
    }
    println(mimeBodyPart.contentType)
    if (mimeBodyPart.content is Multipart) {
        val mp = mimeBodyPart.content as Multipart
        val count = mp.count
        // рекурсия
        for (i in 0 until count) {
            message= "$message\n" +extractTextFromMultiPart(mp.getBodyPart(i) as MimeBodyPart)
        }
    }
    else if (mimeBodyPart.isMimeType("text/plain")||mimeBodyPart.isMimeType("text/html")){
        message = "$message\n  Text: " + Jsoup.parse(mimeBodyPart.content.toString()).text()
    }

    if (mimeBodyPart.description !== null) {
        //  Дополнение к сообщению
        message = "$message\n  Message Description: " +mimeBodyPart.description
    }
    return "$message \n  ]"
}
