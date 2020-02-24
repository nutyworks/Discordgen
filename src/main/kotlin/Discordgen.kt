import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Instant
import kotlin.collections.HashMap

fun main(args: Array<String>) {

    val jda = JDABuilder()
            .setToken(args[0])
            .setStatus(OnlineStatus.ONLINE)
            .setActivity(Activity.playing("코드젠"))
            .addEventListeners(DiscordgenListenerAdapter())
            .build()

    println("Logged in with ${jda.selfUser.name}")

    Thread {
        while (true) {
            Thread.sleep(10000)
            jda.presence.setPresence(Activity.playing("코드젠"), false)
            Thread.sleep(10000)
            jda.presence.setPresence(Activity.playing("디미젠 [번호] 입력 ㄱㄱ　　　　"), false)
        }
    }
}

enum class ProblemErrorType(val str: String) {
    NONE("없음"),
    ANSWER("답안 오류"),
    QUESTION("문제 오류"),
    DUPLICATED("**중복 답안**");
}

class DiscordgenListenerAdapter : ListenerAdapter() {
    class SolutionSet(var type: ProblemErrorType, var reason: String, var example: List<String>, var solution: String)

    companion object {
        val solutions= HashMap<Int, SolutionSet>().apply {
            put(1046, SolutionSet(ProblemErrorType.NONE,
                    "음수가 아닐 때는 추가 줄(`\\n`)이 없음",
                    listOf("-5\n음수입니다.", "10"),
                    "`\\n음수입니다.` 사용"))

            put(1073, SolutionSet(ProblemErrorType.ANSWER,
                    "마지막 `계속하시겠습니까?` 뒤에 공백이 없음",
                    listOf("밑변의 길이 = 11\n높이 = 5\n입력한 삼각형의 넓이는 27.5입니다.\n계속하시겠습니까? Y\n밑변의 길이 = 10\n높이 = 10\n입력한 삼각형의 넓이는 50.0입니다.\n계속하시겠습니까?N"),
                    "https://cafe.naver.com/dimigofreshman/4589"))

            put(1075, SolutionSet(ProblemErrorType.NONE,
                    "`20` 뒤에 공백이 없음",
                    listOf("10 11 12 13 14 15 16 17 18 19 20"),
                    "마지막 수는 예외 처리\n"))

            put(1080, SolutionSet(ProblemErrorType.NONE,
                    "각 줄의 끝에 공백이 없음",
                    listOf("2 3 4 5 6\n3 4 5 6 7\n4 5 6 7 8\n5 6 7 8 9\n6 7 8 9 10"),
                    "각 줄의 마지막 수는 예외 처리"))

            put(1083, SolutionSet(ProblemErrorType.ANSWER,
                    "입력 값이 `28`일 때 출력에 공백이 없음",
                    listOf("28", "22 23 24 25 26 27 28 29 30 31 32 33 34 "),
                    "예외 처리"))

            put(1089, SolutionSet(ProblemErrorType.QUESTION,
                    "각 줄의 끝에 공백이 있음",
                    listOf("1 2 3 4 \n2 4 6 8 \n3 6 9 12 "),
                    "-"))

            put(1098, SolutionSet(ProblemErrorType.ANSWER,
                    "입력 값이 `3`이 아닐 때 각 줄의 끝에 공백이 있음",
                    listOf("1 2 3 A\n4 5 B C\n6 D E F", "1 2 3 4 5 A \n6 7 8 9 B C \n10 11 12 D E F \n13 14 G H I J \n15 K L M N O "),
                    "예외 처리"))

            put(1104, SolutionSet(ProblemErrorType.QUESTION,
                    "각 줄의 끝에 공백이 있음",
                    listOf("    1 \n  1 2 \n1 2 3 "),
                    "-"))

            put(1111, SolutionSet(ProblemErrorType.ANSWER,
                    "입력 값이 `10`일 때 각 줄의 끝에 공백이 없음",
                    listOf("3 4 5\n6 8 10", "3 4 5 \n5 12 13 \n6 8 10 \n7 24 25 \n8 15 17 \n9 12 15 \n10 24 26 \n12 16 20 \n15 20 25 \n20 21 29 "),
                    "예외 처리"))

            put(1112, SolutionSet(ProblemErrorType.ANSWER,
                    "입력 값이 `5 3 9 4 0 0`일 때 첫 번째 줄의 끝에 공백이 없음",
                    listOf("8 2 15 1\n13 5 36 2 ", "21 15 54 6 \n20 16 36 9 \n29 1 210 1 \n26 2 168 1 "),
                    "입력 값이 `5 3 9 4 0 0`일 때 예외 처리"))

            put(1113, SolutionSet(ProblemErrorType.QUESTION,
                    "출력의 마지막에 띄어쓰기가 있음",
                    listOf("2 2 3 3 ", "2689 ", "9439 "),
                    "-"))

            put(1114, SolutionSet(ProblemErrorType.NONE,
                    "출력의 마지막에 공백이 없음\n돈이 0원이 되면 출력하지 않음",
                    listOf("2 1 0 1 1 0 3 1 3", "0 1 4 1 2 1 0 1 4", "0 1 3 0 4 1 1 1"),
                    "마지막 수는 예외 처리"))

            put(1115, SolutionSet(ProblemErrorType.QUESTION,
                    "입력이 `7`이 아닐 때 `*` 사이에 공백이 없음",
                    listOf("* * * * * * *\n* * *   * * *\n* *       * *\n*           *\n* *       * *\n* * *   * * *\n* * * * * * *", "*********\n**** ****\n***   ***\n**     **\n*       *\n**     **\n***   ***\n**** ****\n*********"),
                    "입력이 `7`일 때 예외 처리"))

            put(1124, SolutionSet(ProblemErrorType.ANSWER,
                    "입력이 `95 100 88 65 76 89 58 93 77 99`일 때 출력의 마지막에 공백이 없음\n",
                    listOf("100 99 95 93 89 88 77 76 65 58", "91 80 75 43 42 41 32 23 10 4 ", "94 90 81 49 38 34 33 28 22 22 "),
                    "입력이 `95 100 88 65 76 89 58 93 77 99`일 때 예외 처리"))

            put(1128, SolutionSet(ProblemErrorType.ANSWER,
                    "입력이 `30 20 10 60 80 -1`, `24 -1`일 때 출력의 마지막에 공백이 없음\n",
                    listOf("18 39 36 ", "24", "10 60 80", "27 46 89 "),
                    "입력이 `30 20 10 60 80 -1`, `24 -1`일 때 예외 처리"))

            put(1139, SolutionSet(ProblemErrorType.DUPLICATED,
                    "입력이 `1 2 3 4 5 6 7 8 1 4 7 8 3 6 9 8`일 때 중복 답안 중 하나는 각 줄의 마지막에 공백이 있음\n",
                    listOf("1 8 21 32\n15 36 63 64", "1 8 21 32 \n15 36 63 64 ", "2068 5088 4697 1020 \n156 990 1580 504 "),
                    "난수로 처리 (도움말: 디미젠 난수처리)"))

            put(1145, SolutionSet(ProblemErrorType.QUESTION,
                    "각 줄의 끝에 공백이 있음",
                    listOf("3 5 9 \n2 11 5 \n8 30 10 \n22 5 1 \n111"),
                    "-"))

            put(1148, SolutionSet(ProblemErrorType.ANSWER,
                    "입력이 `3 6 9 8 5 2 9 8 7 6 5 4`일 때 각 줄의 끝에 공백이 없음",
                    listOf("27 48 63\n48 25 8"),
                    "예외 처리"))

            put(1149, SolutionSet(ProblemErrorType.ANSWER,
                    "첫 번째 줄의 끝에 공백이 있음\n입력이 `16 27 39 100 19 88 61 20`일 때 첫 번째 줄의 끝에 공백이 없음",
                    listOf("가로평균 21 69 53 40\n세로평균 33 58\n전체평균 46", "가로평균 67 21 50 63 \n세로평균 34 66\n전체평균 50"),
                    "예외 처리"))

            put(1151, SolutionSet(ProblemErrorType.QUESTION,
                    "입력이 `A B C D E F G H I J K L M N O`일 때 각 줄의 끝에 공백이 없음",
                    listOf("a b c d e\nf g h i j\nk l m n o"),
                    "예외 처리"))

            put(1162, SolutionSet(ProblemErrorType.DUPLICATED,
                    "입력이 `3 5`일 때 중복 답안\n중복 답안 중 하나는 `5 * 8 = 40` 뒤에 공백이 있음",
                    listOf("== 3단 ==\n3 * 1 =  3\n3 * 2 =  6\n3 * 3 =  9\n3 * 4 = 12\n3 * 5 = 15\n3 * 6 = 18\n3 * 7 = 21\n3 * 8 = 24\n3 * 9 = 27\n\n== 4단 ==\n4 * 1 =  4\n4 * 2 =  8\n4 * 3 = 12\n … \n5 * 8 = 40 \n5 * 9 = 45",
                            "== 3단==\n3 * 1 =  3\n3 * 2 =  6\n3 * 3 =  9\n3 * 4 = 12\n3 * 5 = 15\n3 * 6 = 18\n3 * 7 = 21\n3 * 8 = 24\n3 * 9 = 27\n\n== 4단==\n4 * 1 =  4\n4 * 2 =  8\n4 * 3 = 12\n4 * 4 = 16\n4 * 5 = 20\n4 * 6 = 24\n4 * 7 = 28\n4 * 8 = 32\n4 * 9 = 36\n\n== 5단==\n5 * 1 =  5\n5 * 2 = 10\n5 * 3 = 15\n5 * 4 = 20\n5 * 5 = 25\n5 * 6 = 30\n5 * 7 = 35\n5 * 8 = 40\n5 * 9 = 45"),
                    "난수로 처리 (도움말: 디미젠 난수처리)"))

            put(1165, SolutionSet(ProblemErrorType.DUPLICATED,
                    "입력이 `3`일 때 중복 답안",
                    listOf("1 2 3\n4 5 6\n7 8 9", "1 2 3 \n4 5 6 \n7 8 9 "),
                    "난수로 처리 (도움말: 디미젠 난수처리)"))
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val bot = event.author.isBot
        val isDimiGuild = event.guild.id == "665163164471787530"
        val isTestGuild = event.guild.id == "672409379747659786"

        if(bot) {
//            println("${event.author.asTag} tried to perform command but rejected; bot.")
            return
        }

        if(!isDimiGuild && !isTestGuild) return

        val args = event.message.contentRaw.split(Regex("\\s+"))
        val isDimigen = args[0] == "디미젠"
        val isReactionGet = args[0] == "!r"
        val hasFirstArgument = args.size >= 2
        val isValidProblemNumber = if (hasFirstArgument) args[1].matches(Regex("^[0-9]{4}$")) else false
        val isValidContestNumber = if (hasFirstArgument) args[1].matches(Regex("^[A-Za-d]$")) else false
        val isHowToNansu = if(hasFirstArgument) args[1] == "난수처리" else false
        val problemNumber = if (isValidProblemNumber) { Integer.parseInt(args[1]) } else if (isValidContestNumber) { getContestNum(args[1][0]) } else -1
        val hasSolution = if(problemNumber != -1) solutions.containsKey(problemNumber) else false

        if(isDimigen) {
            if (hasFirstArgument) {
                if (problemNumber != -1) {
                    if (hasSolution) {
                        val solution = solutions[problemNumber]!!
                        val dimigenHelper = EmbedBuilder()
                                .setColor(0x7070FF)
                                .setTitle("코드젠 문제 #$problemNumber", "http://codegen.dimigo.hs.kr/problem.php?id=$problemNumber")
                                .addField("분류", solution.type.str, true)
                                .apply {
                                    val res = httpGet("http://codegen.dimigo.hs.kr/problem.php?id=$problemNumber")

                                    val acceptedRegex = Regex("제출: </span>(\\d+)&nbsp;&nbsp;<span class=green>해결: </span>(\\d+)")
                                    var acceptedMatch = acceptedRegex.find(res)?.groupValues

                                    if (acceptedMatch == null) {
                                        val redirectMatch = Regex("This problem is in Contest\\(s\\) below:<br><a href=([a-z0-9&=.?]+)>Contest").find(res)?.groupValues

                                        val reres = httpGet("http://codegen.dimigo.hs.kr/" + redirectMatch?.get(1))

                                        acceptedMatch = acceptedRegex.find(reres)?.groupValues
                                        addField("AC/제출", "${acceptedMatch?.get(2)}/${acceptedMatch?.get(1)}", true)
                                    } else {
                                        addField("AC/제출", "${acceptedMatch[2]}/${acceptedMatch[1]}", true)
                                    }
                                }
                                .addField("사유", solution.reason, false)
                                .apply {
                                    var str = ""

                                    solutions[problemNumber]!!.example.forEach {
                                        str += "```tex\n$$>>>>>>>>>>>>>>>\n$it\n>>>>>>>>>>>>>>>$$```"
                                    }

                                    addField("예시", str, false)
                                }
                                .addField("해결법", solution.solution, false)
                                .setFooter("디스코드젠")
                                .setTimestamp(Instant.now())
                                .build()

                        event.textChannel.sendMessage(dimigenHelper).queue {
                            println("${event.author.name} requested #$problemNumber; answered.")
                        }
                    } else {
                        val noSolution = EmbedBuilder()
                                .addField("#$problemNumber", "존재하지 않거나 오류가 없는 문제입니다.\n오류라고 생각하신다면 <@627050604673368064>에게 문의해주세요.\n(1165번 이후 문제는 작업 중)", true)
                                .setColor(0x7070FF)
                                .build()
                        event.textChannel.sendMessage(noSolution).queue() {
                            println("${event.author.name} requested #$problemNumber; #$problemNumber not found.")
                        }
                    }
                } else if(isHowToNansu) {
                    val howToNansu = EmbedBuilder()
                            .setColor(0x7070FF)
                            .setTitle("난수 처리 방법")
                            .addField("시간 지연하기", "```c\n" +
                                    "#include <stdlib.h>\n" +
                                    "#include <time.h>\n" +
                                    "\n" +
                                    "int main()\n" +
                                    "{\n" +
                                    "    ...\n" +
                                    "    _sleep(500);\n" +
                                    "    srand(time(NULL));\n" +
                                    "    ...\n" +
                                    "}```", true)
                            .addField("포인터", "```c\n" +
                                    "#include <stdlib.h>\n" +
                                    "\n" +
                                    "int main()\n" +
                                    "{\n" +
                                    "    ...\n" +
                                    "    int s;\n" +
                                    "    srand((unsigned int)&s);\n" +
                                    "    ...\n" +
                                    "}```", true)
                            .build()

                    event.channel.sendMessage(howToNansu).queue()
                } else {
                    val wrongNumber = EmbedBuilder()
                            .addField("오류", "잘못된 문제 번호", true)
                            .setColor(0xff0000)
                            .build()

                    event.textChannel.sendMessage(wrongNumber).queue() {
                        println("${event.author.name} requested #${args[1]}; invalid number.")
                    }
                }
            } else {
                val dimigenUsage = EmbedBuilder()
                        .addField("도움말", "디미젠 [문제 번호]", true)
                        .setColor(0x7070FF)
                        .build()
                event.textChannel.sendMessage(dimigenUsage).queue()
            }
        }

        if(isReactionGet) {
            val isOwner = event.author.idLong == 627050604673368064

            if(isOwner) {
                println(event.message.contentRaw)
            }
        }
    }

    fun httpGet(url: String): String {
        val httpClient = HttpClients.createMinimal()
        val httpGet = HttpGet(url)
        httpGet.addHeader("User-Agent", "Mozila/5.0")
        httpGet.addHeader("Content-type", "text/html; charset=utf-8")
        val response = httpClient.execute(httpGet)
        val reader = BufferedReader(InputStreamReader(response.entity.content))

        val r = reader.readText()

        httpClient.close()
        reader.close()

        return r
    }

    fun getContestNum(c: Char): Int {
        if(c < 'G') return 1084 - 'A'.toByte() + c.toByte()
        else if(c <= 'Z') return 1092 - 'G'.toByte() + c.toByte()
        when(c) {
            'a' -> return 1112
            'b' -> return 1113
            'c' -> return 1114
            'd' -> return 1115
        }

        return -1
    }
}

class Discordgen {
}