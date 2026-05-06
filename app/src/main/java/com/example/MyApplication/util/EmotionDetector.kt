package com.example.myapplication.util

import android.util.Log

private const val TAG = "Diary.EmotionDetector"

/**
 * Simple keyword-based emotion detector.
 * Scores the input text against curated keyword lists, picks the highest-scoring
 * emotion, and falls back to NEUTRAL if nothing matches.
 */
object EmotionDetector {

    private val happyKeywords = setOf(
        "happy", "joy", "joyful", "wonderful", "great", "amazing", "love", "loved",
        "smile", "smiling", "laugh", "laughed", "laughing", "fun", "good", "best",
        "beautiful", "grateful", "thankful", "blessed", "celebrate", "celebrated",
        "win", "won", "delighted", "cheerful", "excited", "fantastic", "glorious",
        "proud", "pride", "enjoy", "enjoyed", "enjoying", "relief", "relieved",
        "comfortable", "lucky", "glad", "pleased", "thrilled", "bright", "positive",
        "mast", "bindaas", "bindass", "khush", "khushi", "sukhi", "glowing",
        "beaming", "slay", "slaying", "slayed", "wholesome", "smitten",
        "vibing", "vibe", "mauj", "thriving", "elated",
        "shukar", "shukriya", "alhamdulillah", "waheguru", "satnam",
        "chuffed", "euphoric", "blissful", "fortunate", "giggly", "tickled", "kicked",
        "jubilant", "exuberant", "ebullient", "buoyant", "gratified", "rapturous",
        "sanguine", "blithe"
    )

    private val sadKeywords = setOf(
        "sad", "cry", "cried", "crying", "miss", "missed", "missing", "loss",
        "lonely", "loneliness", "grief", "grieve", "hurt", "pain", "tears", "tear",
        "heartbreak", "heartbroken", "disappointed", "disappointment", "depress",
        "depressed", "depression", "down", "upset", "sorry", "regret", "regretful",
        "gone", "alone", "empty", "hopeless", "unfortunate", "difficult", "tough",
        "struggle", "struggled", "struggling", "broken", "lost", "mourn", "mourning",
        "shattered", "devastated", "hollow", "drained", "numb", "lifeless",
        "sobbing", "weeping", "akela", "taanha", "udaas", "dukhi", "yaad",
        "dard", "gham", "takleef", "toot", "tuta", "rona", "ghosted", "low",
        "misss", "missingg",
        "gutted", "crushed", "wrecked", "depleted", "isolated", "abandoned",
        "defeated", "deflated", "blue", "forlorn", "wistful", "melancholy",
        "melancholic", "mournful", "bereft", "crestfallen", "morose", "desolate",
        "rueful", "unwell", "homesick"
    )

    private val anxiousKeywords = setOf(
        "anxious", "anxiety", "worried", "worry", "worrying", "nervous", "nervousness",
        "stress", "stressed", "stressful", "scared", "fear", "fearful", "panic",
        "panicked", "overwhelmed", "uneasy", "dread", "dreading", "tense", "tension",
        "concern", "concerned", "afraid", "terrified", "terror", "unsure", "uncertain",
        "uncertainty", "doubt", "doubtful", "overthinking", "restless", "apprehensive",
        "pareshan", "chinta", "fikar", "ghabrahat", "frazzled", "scattered",
        "jittery", "spiraling", "ghutan", "ghabraana",
        "stressing", "panicking", "fretting", "frantic", "antsy", "foreboding",
        "agitated", "perturbed", "flustered", "vexed", "fraught", "distraught",
        "harried", "discombobulated", "trepidation", "disquiet", "disquieted",
        "doomscrolling", "burnout"
    )

    private val calmKeywords = setOf(
        "calm", "peaceful", "peace", "quiet", "relaxed", "relax", "relaxing",
        "serene", "serenity", "still", "stillness", "meditate", "meditation",
        "breathe", "breathing", "gentle", "slow", "cozy", "comfortable", "content",
        "contentment", "ease", "tranquil", "tranquility", "mindful", "mindfulness",
        "grounded", "centered", "balanced", "harmony", "soothing", "restful", "rested",
        "sukoon", "shanti", "aaram", "shaant", "chill", "yoga", "satsang", "japa",
        "settled", "mellow", "placid", "composed", "unruffled", "sedate",
        "halcyon", "lulled", "zen"
    )

    private val excitedKeywords = setOf(
        "excited", "excitement", "thrilled", "thrill", "incredible", "awesome",
        "pumped", "energized", "ecstatic", "electric", "buzzing", "wow", "amazing",
        "unbelievable", "spectacular", "fantastic", "brilliant", "outstanding",
        "phenomenal", "extraordinary", "can't wait", "looking forward", "adventure",
        "spontaneous", "surprise", "surprised", "exhilarated", "hyped", "fired up",
        "hype", "giddy", "jhakaas", "dhamaal", "geeked", "winning",
        "exciteddd", "excitee", "omg", "stoked",
        "jazzed", "amped", "psyched", "eager", "agog", "enraptured", "enthralled",
        "riveted", "effervescent", "rapt", "galvanized", "invigorated"
    )

    /**
     * Detects the dominant emotional tone of the given text.
     * Returns one of: "HAPPY", "SAD", "ANXIOUS", "CALM", "EXCITED", or "NEUTRAL"
     */
    fun detect(text: String): String {
        if (text.isBlank()) {
            Log.d(TAG, "detect: blank input → NEUTRAL")
            return "NEUTRAL"
        }

        val words = text
            .lowercase()
            .replace(Regex("[^a-z\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .toSet()

        val scores = mapOf(
            "HAPPY"   to words.count { it in happyKeywords },
            "SAD"     to words.count { it in sadKeywords },
            "ANXIOUS" to words.count { it in anxiousKeywords },
            "CALM"    to words.count { it in calmKeywords },
            "EXCITED" to words.count { it in excitedKeywords }
        )
        Log.d(TAG, "detect: wordCount=${words.size} scores=$scores")

        val best = scores.maxByOrNull { it.value }
        val result = if (best != null && best.value > 0) best.key else "NEUTRAL"
        Log.d(TAG, "detect: result=$result")
        return result
    }
}
