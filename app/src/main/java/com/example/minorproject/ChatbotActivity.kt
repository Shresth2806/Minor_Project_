package com.example.minorproject

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale
import kotlin.math.max

class ChatbotActivity : AppCompatActivity() {

    private lateinit var chatContainer: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var typingView: TextView

    private val handler = Handler(Looper.getMainLooper())

    private val allRecords = mutableListOf<DiseaseRecord>()
    private val conversationSymptoms = linkedSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chatbot)

        setupViews()
        setupSystemBars()
        loadDataset()
        setupListeners()

        addBotMessage(
            "Hello! I'm your Health Assistant 👋\n\n" +
                    "Tell me what you're experiencing in your own words. " +
                    "For example:\n\n" +
                    "• I have fever and headache\n" +
                    "• My stomach hurts and I feel nauseous\n" +
                    "• I have itching and a skin rash\n\n" +
                    "I'll ask a few questions and then show possible conditions based on your symptoms."
        )
    }

    private fun setupViews() {

        chatContainer = findViewById(R.id.chatContainer)
        scrollView = findViewById(R.id.chatScrollView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        typingView = findViewById(R.id.typingView)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.btnClearChat).setOnClickListener {
            clearConversation()
        }
    }

    private fun setupSystemBars() {

        val root = findViewById<View>(R.id.chatbotRoot)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->

            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                view.paddingLeft,
                bars.top,
                view.paddingRight,
                bars.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(root)
    }

    private fun setupListeners() {

        sendButton.setOnClickListener {
            sendMessage()
        }

        messageInput.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }

    private fun sendMessage() {

        val message = messageInput.text.toString().trim()

        if (message.isEmpty()) {
            return
        }

        addUserMessage(message)

        messageInput.text.clear()

        typingView.visibility = View.VISIBLE
        scrollToBottom()

        handler.postDelayed({

            typingView.visibility = View.GONE

            processUserMessage(message)

        }, 700)
    }

    private fun processUserMessage(message: String) {

        val normalized = normalize(message)

        if (isEmergencyMessage(normalized)) {

            addBotMessage(
                "Your message contains symptoms that may require urgent medical attention.\n\n" +
                        "Please don't rely on this chatbot for an emergency. " +
                        "Contact your local emergency service or go to the nearest emergency department immediately.\n\n" +
                        "If you are having severe difficulty breathing, severe chest pain, " +
                        "loss of consciousness, or sudden weakness, seek emergency help now."
            )

            return
        }

        if (isGreeting(normalized)) {

            addBotMessage(
                "Hello! I'm here to help you understand your symptoms. " +
                        "Tell me what you're feeling, even if you're not sure of the medical term."
            )

            return
        }

        if (isHelpRequest(normalized)) {

            addBotMessage(
                "Of course. Just describe your symptoms naturally.\n\n" +
                        "For example: \"I've had a high fever, headache and body pain since yesterday.\"\n\n" +
                        "You don't need to use medical terminology."
            )

            return
        }

        val detectedSymptoms = findSymptoms(message)

        if (detectedSymptoms.isNotEmpty()) {

            conversationSymptoms.addAll(detectedSymptoms)

            val readableSymptoms = detectedSymptoms.joinToString(", ") {
                formatSymptom(it)
            }

            addBotMessage(
                "I understood these symptoms:\n\n" +
                        "• $readableSymptoms\n\n" +
                        "Let me compare them with the symptom information in my medical dataset."
            )

            handler.postDelayed({

                val results = findBestMatches(conversationSymptoms.toList())

                if (results.isEmpty()) {

                    addBotMessage(
                        "I couldn't find a strong enough match in the current dataset.\n\n" +
                                "Try describing another symptom, such as when it started, " +
                                "where you feel it, or any additional symptoms."
                    )

                } else {

                    showResults(results)
                }

            }, 500)

        } else {

            addBotMessage(
                "I want to understand you correctly. Could you describe the physical symptoms you're experiencing?\n\n" +
                        "For example:\n" +
                        "• fever\n" +
                        "• headache\n" +
                        "• cough\n" +
                        "• stomach pain\n" +
                        "• vomiting\n" +
                        "• itching\n\n" +
                        "You can also write a complete sentence."
            )
        }
    }

    private fun loadDataset() {

        try {

            val inputStream = assets.open("DiseaseAndSymptoms.csv")

            val reader = BufferedReader(
                InputStreamReader(inputStream)
            )

            reader.readLine()

            var line: String?

            while (reader.readLine().also { line = it } != null) {

                val columns = parseCsvLine(line ?: "")

                if (columns.isEmpty()) {
                    continue
                }

                val disease = columns[0].trim()

                if (disease.isEmpty()) {
                    continue
                }

                val symptoms = mutableListOf<String>()

                for (i in 1 until minOf(columns.size, 18)) {

                    val symptom = columns[i].trim()

                    if (symptom.isNotEmpty()) {
                        symptoms.add(normalize(symptom))
                    }
                }

                allRecords.add(
                    DiseaseRecord(
                        disease = disease,
                        symptoms = symptoms
                    )
                )
            }

            reader.close()

        } catch (e: Exception) {

            addBotMessage(
                "I'm having trouble loading the symptom database. " +
                        "Please make sure DiseaseAndSymptoms.csv is inside:\n\n" +
                        "app/src/main/assets/"
            )
        }
    }

    private fun findSymptoms(message: String): List<String> {

        if (allRecords.isEmpty()) {
            return emptyList()
        }

        val datasetSymptoms = linkedSetOf<String>()

        allRecords.forEach { record ->
            datasetSymptoms.addAll(record.symptoms)
        }

        val normalizedMessage = normalize(message)
        val messageTokens = tokenize(normalizedMessage)

        val found = mutableListOf<Pair<String, Double>>()

        for (symptom in datasetSymptoms) {

            val symptomTokens = tokenize(symptom)

            if (symptomTokens.isEmpty()) {
                continue
            }

            var score = 0.0

            if (normalizedMessage.contains(symptom)) {
                score = 1.0
            } else {

                val matchingTokens = symptomTokens.count { token ->

                    messageTokens.any { messageToken ->

                        messageToken == token ||
                                levenshteinDistance(
                                    messageToken,
                                    token
                                ) <= max(1, token.length / 5)
                    }
                }

                score = matchingTokens.toDouble() / symptomTokens.size
            }

            val aliasScore = aliasMatch(
                normalizedMessage,
                symptom
            )

            score = max(score, aliasScore)

            if (score >= 0.65) {
                found.add(symptom to score)
            }
        }

        return found
            .sortedByDescending { it.second }
            .take(6)
            .map { it.first }
    }

    private fun aliasMatch(
        message: String,
        symptom: String
    ): Double {

        val aliases = mapOf(

            "high fever" to listOf(
                "high temperature",
                "very high fever",
                "temperature",
                "burning fever"
            ),

            "stomach pain" to listOf(
                "stomach ache",
                "pain in stomach",
                "belly pain",
                "abdominal pain",
                "pain in my belly"
            ),

            "headache" to listOf(
                "head pain",
                "pain in my head",
                "my head hurts"
            ),

            "vomiting" to listOf(
                "throwing up",
                "threw up",
                "feeling like vomiting",
                "puking",
                "puke"
            ),

            "nausea" to listOf(
                "feeling sick",
                "feel sick",
                "want to vomit"
            ),

            "cough" to listOf(
                "coughing",
                "i am coughing",
                "keep coughing"
            ),

            "itching" to listOf(
                "itchy",
                "skin itching",
                "my skin itches"
            ),

            "skin rash" to listOf(
                "rash",
                "red rash",
                "skin rash",
                "rash on skin"
            ),

            "chest pain" to listOf(
                "pain in chest",
                "my chest hurts",
                "chest hurts"
            ),

            "back pain" to listOf(
                "pain in back",
                "my back hurts"
            ),

            "joint pain" to listOf(
                "pain in joints",
                "joint aches",
                "aching joints"
            ),

            "fatigue" to listOf(
                "very tired",
                "extremely tired",
                "low energy",
                "weakness",
                "feeling weak"
            ),

            "dizziness" to listOf(
                "dizzy",
                "feeling dizzy",
                "light headed",
                "lightheaded"
            ),

            "breathlessness" to listOf(
                "shortness of breath",
                "difficulty breathing",
                "hard to breathe",
                "can't breathe properly"
            )
        )

        val possibleAliases = aliases[symptom] ?: return 0.0

        return if (
            possibleAliases.any { alias ->
                message.contains(alias)
            }
        ) {
            0.95
        } else {
            0.0
        }
    }

    private fun findBestMatches(
        symptoms: List<String>
    ): List<DiseaseResult> {

        if (symptoms.isEmpty()) {
            return emptyList()
        }

        val results = mutableMapOf<String, MutableSet<String>>()

        for (record in allRecords) {

            val matched = symptoms.filter {
                record.symptoms.contains(it)
            }

            if (matched.isNotEmpty()) {

                val set = results.getOrPut(record.disease) {
                    mutableSetOf()
                }

                set.addAll(matched)
            }
        }

        return results.map { (disease, matchedSymptoms) ->

            val totalSymptomsForDisease =
                allRecords
                    .filter { it.disease.equals(disease, true) }
                    .flatMap { it.symptoms }
                    .toSet()
                    .size

            val score =
                matchedSymptoms.size.toDouble() /
                        max(1, symptoms.size).toDouble()

            DiseaseResult(
                disease = disease,
                matchedSymptoms = matchedSymptoms.toList(),
                score = score,
                datasetCoverage = matchedSymptoms.size.toDouble() /
                        max(1, totalSymptomsForDisease)
            )

        }
            .sortedWith(
                compareByDescending<DiseaseResult> {
                    it.score
                }.thenByDescending {
                    it.matchedSymptoms.size
                }
            )
            .take(3)
    }

    private fun showResults(
        results: List<DiseaseResult>
    ) {

        val top = results.first()

        val percentage =
            (top.score * 100).toInt().coerceIn(0, 100)

        val specialization =
            getSpecialization(top.disease)

        var message =
            "Based on the symptoms you've described, " +
                    "the strongest dataset match is:\n\n" +
                    "Possible condition: ${top.disease}\n" +
                    "Symptom match: $percentage%\n" +
                    "Suggested specialist: $specialization\n\n" +
                    "Symptoms matched:\n"

        top.matchedSymptoms.forEach {
            message += "• ${formatSymptom(it)}\n"
        }

        if (results.size > 1) {

            message += "\nOther possible matches:\n"

            results.drop(1).forEach {

                val score =
                    (it.score * 100).toInt().coerceIn(0, 100)

                message +=
                    "• ${it.disease} — $score% match\n"
            }
        }

        message +=
            "\nThis is a symptom-based recommendation, not a medical diagnosis. " +
                    "A qualified healthcare professional should evaluate your condition."

        addBotMessage(message)

        handler.postDelayed({

            addBotMessage(
                "If you'd like, tell me whether your symptoms are getting better, " +
                        "getting worse, or staying the same. I can use that information " +
                        "to guide the next step."
            )

        }, 700)
    }

    private fun getSpecialization(
        disease: String
    ): String {

        val name = disease.lowercase(Locale.US)

        return when {

            name.contains("skin") ||
                    name.contains("fungal") ||
                    name.contains("acne") ||
                    name.contains("psoriasis") ->
                "Dermatologist"

            name.contains("heart") ||
                    name.contains("cardiac") ->
                "Cardiologist"

            name.contains("lung") ||
                    name.contains("asthma") ||
                    name.contains("pneumonia") ||
                    name.contains("bronch") ->
                "Pulmonologist"

            name.contains("stomach") ||
                    name.contains("gastric") ||
                    name.contains("ulcer") ||
                    name.contains("hepat") ->
                "Gastroenterologist"

            name.contains("kidney") ||
                    name.contains("urinary") ->
                "Urologist"

            name.contains("brain") ||
                    name.contains("migraine") ||
                    name.contains("paralysis") ->
                "Neurologist"

            name.contains("diabetes") ->
                "Endocrinologist"

            name.contains("joint") ||
                    name.contains("arthritis") ->
                "Orthopedic Specialist"

            else ->
                "General Physician"
        }
    }

    private fun isGreeting(
        message: String
    ): Boolean {

        val greetings = listOf(
            "hello",
            "hi",
            "hey",
            "good morning",
            "good afternoon",
            "good evening"
        )

        return greetings.any {
            message == it ||
                    message.startsWith("$it ")
        }
    }

    private fun isHelpRequest(
        message: String
    ): Boolean {

        return message.contains("help me") ||
                message.contains("what can you do") ||
                message.contains("how does this work")
    }

    private fun isEmergencyMessage(
        message: String
    ): Boolean {

        val emergencyWords = listOf(
            "can't breathe",
            "cannot breathe",
            "not breathing",
            "severe chest pain",
            "chest pain and sweating",
            "unconscious",
            "passed out",
            "fainted",
            "stroke",
            "face drooping",
            "slurred speech",
            "severe bleeding",
            "bleeding heavily",
            "suicidal",
            "kill myself"
        )

        return emergencyWords.any {
            message.contains(it)
        }
    }

    private fun normalize(
        text: String
    ): String {

        return text
            .lowercase(Locale.US)
            .replace("_", " ")
            .replace("-", " ")
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun tokenize(
        text: String
    ): List<String> {

        return normalize(text)
            .split(" ")
            .filter {
                it.length >= 3
            }
    }

    private fun formatSymptom(
        symptom: String
    ): String {

        return symptom
            .replace("_", " ")
            .replaceFirstChar {
                if (it.isLowerCase()) {
                    it.titlecase(Locale.US)
                } else {
                    it.toString()
                }
            }
    }

    private fun levenshteinDistance(
        first: String,
        second: String
    ): Int {

        if (first == second) return 0

        if (first.isEmpty()) return second.length

        if (second.isEmpty()) return first.length

        val previous = IntArray(second.length + 1) {
            it
        }

        val current = IntArray(second.length + 1)

        for (i in first.indices) {

            current[0] = i + 1

            for (j in second.indices) {

                val cost =
                    if (first[i] == second[j]) 0 else 1

                current[j + 1] = minOf(
                    current[j] + 1,
                    previous[j + 1] + 1,
                    previous[j] + cost
                )
            }

            for (j in previous.indices) {
                previous[j] = current[j]
            }
        }

        return previous[second.length]
    }

    private fun parseCsvLine(
        line: String
    ): List<String> {

        val result = mutableListOf<String>()
        val current = StringBuilder()
        var insideQuotes = false

        for (character in line) {

            when {

                character == '"' -> {
                    insideQuotes = !insideQuotes
                }

                character == ',' && !insideQuotes -> {
                    result.add(current.toString())
                    current.clear()
                }

                else -> {
                    current.append(character)
                }
            }
        }

        result.add(current.toString())

        return result
    }

    private fun addUserMessage(
        message: String
    ) {

        val bubble = createBubble(
            message = message,
            isUser = true
        )

        chatContainer.addView(bubble)
        scrollToBottom()
    }

    private fun addBotMessage(
        message: String
    ) {

        val bubble = createBubble(
            message = message,
            isUser = false
        )

        chatContainer.addView(bubble)
        scrollToBottom()
    }

    private fun createBubble(
        message: String,
        isUser: Boolean
    ): TextView {

        val textView = TextView(this)

        textView.text = message
        textView.textSize = 15f
        textView.setTextColor(
            if (isUser) Color.WHITE else Color.rgb(30, 42, 64)
        )
        textView.setPadding(
            18,
            14,
            18,
            14
        )

        if (!isUser) {
            textView.setTypeface(
                Typeface.DEFAULT,
                Typeface.NORMAL
            )
        }

        val drawable =
            android.graphics.drawable.GradientDrawable()

        drawable.cornerRadius = 24f

        if (isUser) {
            drawable.setColor(
                Color.rgb(38, 115, 232)
            )
        } else {
            drawable.setColor(
                Color.WHITE
            )

            drawable.setStroke(
                1,
                Color.rgb(225, 231, 240)
            )
        }

        textView.background = drawable

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.setMargins(
            if (isUser) 70 else 0,
            6,
            if (isUser) 0 else 70,
            6
        )

        params.gravity =
            if (isUser) Gravity.END else Gravity.START

        textView.layoutParams = params

        return textView
    }

    private fun scrollToBottom() {

        scrollView.post {
            scrollView.fullScroll(
                View.FOCUS_DOWN
            )
        }
    }

    private fun clearConversation() {

        chatContainer.removeAllViews()
        conversationSymptoms.clear()

        addBotMessage(
            "Conversation cleared.\n\n" +
                    "Tell me what symptoms you're experiencing and we'll start again."
        )
    }

    data class DiseaseRecord(
        val disease: String,
        val symptoms: List<String>
    )

    data class DiseaseResult(
        val disease: String,
        val matchedSymptoms: List<String>,
        val score: Double,
        val datasetCoverage: Double
    )
}