package com.example.data

import com.example.model.QuizQuestion

object MockQuestionBankPart2 {
    val questions: List<QuizQuestion> = listOf(
        // UNIT 4 (9 questions)
        QuizQuestion(
            id = "m_u4_01", unitId = "unit_4",
            prompt = "Which 16th-century Spanish work is widely considered the prototype of the Picaresque novel?",
            options = listOf("Don Quixote", "Lazarillo de Tormes", "Amadís de Gaula", "La Celestina"),
            correctIndex = 1,
            explanation = "'Lazarillo de Tormes' (1554) established the episodic first-person narrative of a lower-class rogue (picaro)."
        ),
        QuizQuestion(
            id = "m_u4_02", unitId = "unit_4",
            prompt = "James Joyce's 'A Portrait of the Artist as a Young Man' is a classic example of a:",
            options = listOf("Künstlerroman", "Epistolary novel", "Campus novel", "Silver-fork novel"),
            correctIndex = 0,
            explanation = "A Künstlerroman is a subgenre of Bildungsroman tracing an artist's maturation."
        ),
        QuizQuestion(
            id = "m_u4_03", unitId = "unit_4",
            prompt = "Which dramatic technique did Bertolt Brecht design to prevent audiences from passively identifying with characters?",
            options = listOf("Catharsis", "Verfremdungseffekt (Alienation Effect)", "Hamartia", "Anagnorisis"),
            correctIndex = 1,
            explanation = "Brecht's Epic Theatre employs the Verfremdungseffekt (estrangement effect) to foster critical reflection."
        ),
        QuizQuestion(
            id = "m_u4_04", unitId = "unit_4",
            prompt = "Who pioneered the personal essay in French in 1580 under the title 'Essais'?",
            options = listOf("Francis Bacon", "Michel de Montaigne", "Blaise Pascal", "Voltaire"),
            correctIndex = 1,
            explanation = "Michel de Montaigne coined 'Essais' ('trials' or 'attempts') in 1580; Bacon later adapted the form in English (1597)."
        ),
        QuizQuestion(
            id = "m_u4_05", unitId = "unit_4",
            prompt = "Which 17th-century diarist left a vivid shorthand record of the Great Plague and Great Fire of London?",
            options = listOf("John Evelyn", "Samuel Pepys", "James Boswell", "Richard Steele"),
            correctIndex = 1,
            explanation = "Samuel Pepys kept his famous diary from 1660 to 1669, capturing Restoration London."
        ),
        QuizQuestion(
            id = "m_u4_06", unitId = "unit_4",
            prompt = "In which type of ode do stanzas follow a tripartite structure of Strophe, Antistrophe, and Epode?",
            options = listOf("Horatian Ode", "Pindaric Ode", "Irregular Ode", "Anacreontic Ode"),
            correctIndex = 1,
            explanation = "The Greek choric Pindaric ode uses Strophe, Antistrophe, and Epode (e.g., Thomas Gray's 'The Progress of Poesy')."
        ),
        QuizQuestion(
            id = "m_u4_07", unitId = "unit_4",
            prompt = "Who proposed the concept of 'cognitive estrangement' and the 'novum' to define Science Fiction?",
            options = listOf("Darko Suvin", "Isaac Asimov", "Tzvetan Todorov", "Fredric Jameson"),
            correctIndex = 0,
            explanation = "Darko Suvin defined science fiction in 'Metamorphoses of Science Fiction' (1979) through cognitive estrangement and the novum."
        ),
        QuizQuestion(
            id = "m_u4_08", unitId = "unit_4",
            prompt = "A fictional narrative in which real-life persons and events appear under thin fictional disguises is called a:",
            options = listOf("Bildungsroman", "Roman à clef", "Metafiction", "Pastoral"),
            correctIndex = 1,
            explanation = "'Roman à clef' ('novel with a key') masks real people behind fictional names (e.g., Sylvia Plath's 'The Bell Jar')."
        ),
        QuizQuestion(
            id = "m_u4_09", unitId = "unit_4",
            prompt = "Which dramatic monologue by Robert Browning features a Renaissance Duke showing a portrait of his late wife?",
            options = listOf("Fra Lippo Lippi", "Andrea del Sarto", "My Last Duchess", "Porphyria's Lover"),
            correctIndex = 2,
            explanation = "In 'My Last Duchess' (1842), the Duke of Ferrara reveals his chilling possessiveness to an envoy."
        ),

        // UNIT 5 (8 questions)
        QuizQuestion(
            id = "m_u5_01", unitId = "unit_5",
            prompt = "In John Dryden's tripartite model of translation (1680), 'word-for-word, line-for-line' literal translation is called:",
            options = listOf("Paraphrase", "Metaphrase", "Imitation", "Transcreation"),
            correctIndex = 1,
            explanation = "Dryden distinguished Metaphrase (word-for-word), Paraphrase (sense-for-sense), and Imitation (free adaptation)."
        ),
        QuizQuestion(
            id = "m_u5_02", unitId = "unit_5",
            prompt = "Who developed 'Polysystem Theory', viewing translated literature as a dynamic subsystem within a target culture?",
            options = listOf("Itamar Even-Zohar", "Eugene Nida", "J.C. Catford", "Katharina Reiss"),
            correctIndex = 0,
            explanation = "Itamar Even-Zohar formulated Polysystem Theory in the 1970s, further developed by Gideon Toury."
        ),
        QuizQuestion(
            id = "m_u5_03", unitId = "unit_5",
            prompt = "Eugene Nida's 'Dynamic Equivalence' prioritizes:",
            options = listOf(
                "Mirroring source-language syntax strictly",
                "Producing an equivalent natural effect on the target-language reader",
                "Retaining untranslated foreign terms",
                "Replacing verse only with prose"
            ),
            correctIndex = 1,
            explanation = "Dynamic (or functional) equivalence seeks the closest natural equivalent in the target language so the receptor responds similarly."
        ),
        QuizQuestion(
            id = "m_u5_04", unitId = "unit_5",
            prompt = "Who delivered the 1958 paper 'The Crisis of Comparative Literature', critiquing the French School's focus on factual influence?",
            options = listOf("René Wellek", "Paul Van Tieghem", "Claudio Guillén", "Gayatri Spivak"),
            correctIndex = 0,
            explanation = "René Wellek criticized narrow source-and-influence studies at the 1958 ICLA Congress in Chapel Hill."
        ),
        QuizQuestion(
            id = "m_u5_05", unitId = "unit_5",
            prompt = "Which university founded India's first Department of Comparative Literature in 1956 under Buddhadeva Bose?",
            options = listOf("University of Delhi", "Jadavpur University", "University of Hyderabad", "Visva-Bharati"),
            correctIndex = 1,
            explanation = "Jadavpur University established India's first Department of Comparative Literature in 1956."
        ),
        QuizQuestion(
            id = "m_u5_06", unitId = "unit_5",
            prompt = "Who proposed the method of 'Distant Reading' in 'Conjectures on World Literature' (2000)?",
            options = listOf("David Damrosch", "Franco Moretti", "Pascale Casanova", "Emily Apter"),
            correctIndex = 1,
            explanation = "Franco Moretti advocated 'distant reading'—analyzing large corpora and literary systems—to study world literature."
        ),
        QuizQuestion(
            id = "m_u5_07", unitId = "unit_5",
            prompt = "Hans Vermeer's 'Skopos Theory' asserts that the primary determinant of a translation strategy is:",
            options = listOf("Grammar of the source text", "The purpose (skopos) of the target text", "Rhyme structure", "Authorial biography"),
            correctIndex = 1,
            explanation = "'Skopos' (Greek for 'aim' or 'purpose') states that the function of the translation in the target context governs translation choices."
        ),
        QuizQuestion(
            id = "m_u5_08", unitId = "unit_5",
            prompt = "Which Indian scholar translated classical Tamil Sangam poetry in 'The Interior Landscape' (1967)?",
            options = listOf("Sujit Mukherjee", "A.K. Ramanujan", "Dilip Chitre", "Arvind Krishna Mehrotra"),
            correctIndex = 1,
            explanation = "A.K. Ramanujan's 'The Interior Landscape' introduced akam Sangam Tamil poetry to English readers worldwide."
        ),

        // UNIT 6 (12 questions)
        QuizQuestion(
            id = "m_u6_01", unitId = "unit_6",
            prompt = "Who wrote 'An Apology for Poetry' (1595) in response to Stephen Gosson's 'The School of Abuse'?",
            options = listOf("George Puttenham", "Sir Philip Sidney", "Ben Jonson", "Samuel Daniel"),
            correctIndex = 1,
            explanation = "Sir Philip Sidney defended poetry's moral and imaginative power in 'An Apology for Poetry' (also published as 'The Defence of Poesy')."
        ),
        QuizQuestion(
            id = "m_u6_02", unitId = "unit_6",
            prompt = "Longinus's treatise 'On the Sublime' identifies how many principal sources of sublimity?",
            options = listOf("Three", "Four", "Five", "Six"),
            correctIndex = 2,
            explanation = "Longinus lists five sources: grandeur of thought, vigorous passion, figures of speech, noble diction, and dignified composition."
        ),
        QuizQuestion(
            id = "m_u6_03", unitId = "unit_6",
            prompt = "Matthew Arnold's 'Touchstone Method' in 'The Study of Poetry' recommends:",
            options = listOf(
                "Counting metrical feet mathematically",
                "Comparing passages against lines from great masters like Homer, Dante, and Shakespeare",
                "Tracing historical class struggle",
                "Analyzing reader-response questionnaires"
            ),
            correctIndex = 1,
            explanation = "Arnold advocated keeping short passages of the great masters in mind as touchstones to judge contemporary poetry."
        ),
        QuizQuestion(
            id = "m_u6_04", unitId = "unit_6",
            prompt = "In Ferdinand de Saussure's linguistics, 'Langue' refers to:",
            options = listOf(
                "An individual spoken utterance",
                "The shared, abstract system of linguistic rules and conventions",
                "Historical etymology over centuries",
                "Poetic meter alone"
            ),
            correctIndex = 1,
            explanation = "Saussure distinguished 'langue' (the underlying social system of language) from 'parole' (individual speech acts)."
        ),
        QuizQuestion(
            id = "m_u6_05", unitId = "unit_6",
            prompt = "Mikhail Bakhtin's concept of 'Heteroglossia' describes:",
            options = listOf(
                "Monolingual epic grandeur",
                "The coexistence of multiple social voices, registers, and speech genres inside the novel",
                "Authorial silence",
                "Structural binary oppositions"
            ),
            correctIndex = 1,
            explanation = "Bakhtin highlighted the novel's dialogic polyphony and heteroglossia ('many-voicedness')."
        ),
        QuizQuestion(
            id = "m_u6_06", unitId = "unit_6",
            prompt = "Which Marxist theorist distinguished 'Ideological State Apparatuses' (ISAs) from 'Repressive State Apparatuses' and coined 'Interpellation'?",
            options = listOf("Antonio Gramsci", "Louis Althusser", "Georg Lukács", "Walter Benjamin"),
            correctIndex = 1,
            explanation = "Louis Althusser formulated ISAs and ideological 'interpellation' (hailing) in his 1970 essay."
        ),
        QuizQuestion(
            id = "m_u6_07", unitId = "unit_6",
            prompt = "Jacques Derrida's neologism 'Différance' combines which two French meanings?",
            options = listOf("To speak and to write", "To differ and to defer", "To sign and to erase", "To center and to marginalize"),
            correctIndex = 1,
            explanation = "Derrida's 'différance' plays on 'différer'—meaning both spatial differing and temporal deferring of stable meaning."
        ),
        QuizQuestion(
            id = "m_u6_08", unitId = "unit_6",
            prompt = "In 'Gender Trouble' (1990), Judith Butler argues that gender is:",
            options = listOf("A fixed biological essence", "Performative, constituted through repeated stylized acts", "Purely economic", "Outside language"),
            correctIndex = 1,
            explanation = "Butler theorizes gender performativity—not a stable identity, but an effect produced by reiterated cultural acts."
        ),
        QuizQuestion(
            id = "m_u6_09", unitId = "unit_6",
            prompt = "Edward Said's 'Orientalism' (1978) draws heavily on which theorist's concept of discourse and power/knowledge?",
            options = listOf("Michel Foucault", "Claude Lévi-Strauss", "Northrop Frye", "I.A. Richards"),
            correctIndex = 0,
            explanation = "Said adapts Michel Foucault's notion of discourse (alongside Gramsci's hegemony) to analyze Western representations of the Orient."
        ),
        QuizQuestion(
            id = "m_u6_10", unitId = "unit_6",
            prompt = "Which postcolonial essay famously examines the silencing of Bhuvaneswari Bhaduri and asks 'Can the Subaltern Speak?'",
            options = listOf("Edward Said", "Gayatri Chakravorty Spivak", "Dipesh Chakrabarty", "Ranajit Guha"),
            correctIndex = 1,
            explanation = "Gayatri Chakravorty Spivak published 'Can the Subaltern Speak?' (1988), analyzing epistemic violence and subaltern agency."
        ),
        QuizQuestion(
            id = "m_u6_11", unitId = "unit_6",
            prompt = "Who co-edited 'The Ecocriticism Reader: Landmarks in Literary Ecology' (1996)?",
            options = listOf("Cheryll Glotfelty and Harold Fromm", "Lawrence Buell and Greg Garrard", "Raymond Williams and Terry Eagleton", "Kate Soper and Timothy Morton"),
            correctIndex = 0,
            explanation = "Cheryll Glotfelty and Harold Fromm edited the foundational 1996 anthology 'The Ecocriticism Reader'."
        ),
        QuizQuestion(
            id = "m_u6_12", unitId = "unit_6",
            prompt = "Raymond Williams categorized cultural practices into which three overlapping levels in 'Marxism and Literature'?",
            options = listOf("High, Middle, Low", "Dominant, Residual, Emergent", "Base, Superstructure, Aura", "Signifier, Signified, Referent"),
            correctIndex = 1,
            explanation = "Raymond Williams distinguished Dominant, Residual, and Emergent cultural elements to avoid static base-superstructure determinism."
        ),

        // UNIT 7 (8 questions)
        QuizQuestion(
            id = "m_u7_01", unitId = "unit_7",
            prompt = "Which South Korean author won the 2024 Nobel Prize in Literature?",
            options = listOf("Haruki Murakami", "Han Kang", "Yoko Ogawa", "Can Xue"),
            correctIndex = 1,
            explanation = "Han Kang, author of 'The Vegetarian' and 'Human Acts', won the 2024 Nobel Prize in Literature."
        ),
        QuizQuestion(
            id = "m_u7_02", unitId = "unit_7",
            prompt = "Which novel set aboard the International Space Station won the 2024 Booker Prize?",
            options = listOf("James by Percival Everett", "Orbital by Samantha Harvey", "Creation Lake by Rachel Kushner", "Held by Anne Michaels"),
            correctIndex = 1,
            explanation = "Samantha Harvey's 'Orbital' won the 2024 Booker Prize."
        ),
        QuizQuestion(
            id = "m_u7_03", unitId = "unit_7",
            prompt = "Who won the 2024 International Booker Prize for 'Kairos' (translated by Michael Hofmann)?",
            options = listOf("Jenny Erpenbeck", "Olga Tokarczuk", "Georgi Gospodinov", "Jon Fosse"),
            correctIndex = 0,
            explanation = "German author Jenny Erpenbeck and translator Michael Hofmann won the 2024 International Booker Prize for 'Kairos'."
        ),
        QuizQuestion(
            id = "m_u7_04", unitId = "unit_7",
            prompt = "Who received the Sahitya Akademi Award 2024 in English for the novel 'Spirit Nights'?",
            options = listOf("Neelum Saran Gour", "Easterine Kire", "Anuradha Roy", "Namita Gokhale"),
            correctIndex = 1,
            explanation = "Nagaland-born writer Easterine Kire won the 2024 Sahitya Akademi Award in English for 'Spirit Nights'."
        ),
        QuizQuestion(
            id = "m_u7_05", unitId = "unit_7",
            prompt = "Who were jointly named recipients of the 58th Jnanpith Award (announced in 2024)?",
            options = listOf(
                "Damodar Mauzo and Nilmani Phookan",
                "Gulzar and Jagadguru Rambhadracharya",
                "Amitav Ghosh and Krishna Sobti",
                "Kedarnath Singh and Bhalchandra Nemade"
            ),
            correctIndex = 1,
            explanation = "Urdu poet-lyricist Gulzar and Sanskrit scholar Jagadguru Rambhadracharya were selected for the 58th Jnanpith Award."
        ),
        QuizQuestion(
            id = "m_u7_06", unitId = "unit_7",
            prompt = "Amitav Ghosh's non-fiction book 'The Great Derangement' (2016) examines:",
            options = listOf(
                "Classical Sanskrit poetics",
                "Literature and culture's imaginative failure in addressing climate change",
                "The history of printing in Calcutta",
                "Partition cinema"
            ),
            correctIndex = 1,
            explanation = "'The Great Derangement: Climate Change and the Unthinkable' examines how modern realist fiction sidelines ecological crisis."
        ),
        QuizQuestion(
            id = "m_u7_07", unitId = "unit_7",
            prompt = "Which 2004 work by Sarnath Banerjee is widely cited as India's first commercial graphic novel?",
            options = listOf("Kari", "Corridor", "Bhimayana", "Delhi Calm"),
            correctIndex = 1,
            explanation = "Sarnath Banerjee's 'Corridor' (2004) popularized the Indian graphic novel format alongside earlier work like Orijit Sen's 'River of Stories'."
        ),
        QuizQuestion(
            id = "m_u7_08", unitId = "unit_7",
            prompt = "Which author won the 2022 Nobel Prize in Literature for uncovering 'the roots, estrangements and collective restraints of personal memory'?",
            options = listOf("Louise Glück", "Annie Ernaux", "Abdulrazak Gurnah", "Jon Fosse"),
            correctIndex = 1,
            explanation = "French autofiction writer Annie Ernaux won the 2022 Nobel Prize in Literature."
        )
    )
}
