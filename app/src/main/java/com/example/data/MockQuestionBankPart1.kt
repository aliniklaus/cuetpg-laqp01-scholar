package com.example.data

import com.example.model.QuizQuestion

object MockQuestionBankPart1 {
    val questions: List<QuizQuestion> = listOf(
        // UNIT 1 (16 questions)
        QuizQuestion(
            id = "m_u1_01", unitId = "unit_1",
            prompt = "In Geoffrey Chaucer's 'General Prologue', at which London inn do the twenty-nine pilgrims gather?",
            options = listOf("The Globe Inn", "The Tabard Inn", "The Mermaid Tavern", "The Boar's Head"),
            correctIndex = 1,
            explanation = "The pilgrims assemble at the Tabard Inn in Southwark, hosted by Harry Bailly."
        ),
        QuizQuestion(
            id = "m_u1_02", unitId = "unit_1",
            prompt = "Which early English writer introduced blank verse into English in his translation of Virgil's 'Aeneid'?",
            options = listOf("Thomas Wyatt", "Henry Howard, Earl of Surrey", "Thomas Sackville", "Philip Sidney"),
            correctIndex = 1,
            explanation = "Henry Howard, Earl of Surrey pioneered unrhymed iambic pentameter (blank verse) in translating Books II and IV of the 'Aeneid'."
        ),
        QuizQuestion(
            id = "m_u1_03", unitId = "unit_1",
            prompt = "Which playwright is NOT traditionally counted among the 'University Wits'?",
            options = listOf("Christopher Marlowe", "Robert Greene", "Ben Jonson", "Thomas Nashe"),
            correctIndex = 2,
            explanation = "Ben Jonson did not attend Oxford or Cambridge; the University Wits included Marlowe, Greene, Nashe, Lyly, Peele, and Lodge."
        ),
        QuizQuestion(
            id = "m_u1_04", unitId = "unit_1",
            prompt = "In Edmund Spenser's 'The Faerie Queene', Book I follows which knight representing Holiness?",
            options = listOf("Sir Guyon", "Redcrosse Knight", "Sir Artegall", "Sir Calidore"),
            correctIndex = 1,
            explanation = "Book I narrates the quest of the Redcrosse Knight (Holiness) accompanied by Una."
        ),
        QuizQuestion(
            id = "m_u1_05", unitId = "unit_1",
            prompt = "John Dryden's mock-heroic satire 'Mac Flecknoe' (1682) primarily lampoons which rival playwright?",
            options = listOf("Thomas Shadwell", "Nahum Tate", "Colley Cibber", "William Congreve"),
            correctIndex = 0,
            explanation = "Dryden crowns Thomas Shadwell as heir to the realm of 'Nonsense' in 'Mac Flecknoe'."
        ),
        QuizQuestion(
            id = "m_u1_06", unitId = "unit_1",
            prompt = "In Alexander Pope's 'The Rape of the Lock', who protects Belinda's lock with airy spirits?",
            options = listOf("Ariel and the Sylphs", "Puck and Oberon", "Umbriel and the Gnomes", "Triton and the Nymphs"),
            correctIndex = 0,
            explanation = "Ariel leads the Sylphs who guard Belinda, while the melancholy gnome Umbriel visits the Cave of Spleen."
        ),
        QuizQuestion(
            id = "m_u1_07", unitId = "unit_1",
            prompt = "Which 18th-century novel bears the subtitle 'Or, Virtue Rewarded'?",
            options = listOf("Moll Flanders", "Pamela", "Tom Jones", "Evelina"),
            correctIndex = 1,
            explanation = "Samuel Richardson's epistolary novel 'Pamela; or, Virtue Rewarded' appeared in 1740."
        ),
        QuizQuestion(
            id = "m_u1_08", unitId = "unit_1",
            prompt = "Who published 'A Short View of the Immorality and Profaneness of the English Stage' in 1698?",
            options = listOf("Jeremy Collier", "Richard Steele", "Joseph Addison", "Jonathan Swift"),
            correctIndex = 0,
            explanation = "Clergyman Jeremy Collier attacked Restoration comedy playwrights such as Congreve and Vanbrugh in 1698."
        ),
        QuizQuestion(
            id = "m_u1_09", unitId = "unit_1",
            prompt = "Which poem opens the 1798 edition of 'Lyrical Ballads'?",
            options = listOf("Tintern Abbey", "The Rime of the Ancient Mariner", "We Are Seven", "Michael"),
            correctIndex = 1,
            explanation = "Coleridge's 'The Rime of the Ancyent Marinere' opened the 1798 edition, while Wordsworth's 'Tintern Abbey' closed it."
        ),
        QuizQuestion(
            id = "m_u1_10", unitId = "unit_1",
            prompt = "In which work does S.T. Coleridge distinguish between Primary Imagination, Secondary Imagination, and Fancy?",
            options = listOf("Aids to Reflection", "Biographia Literaria", "Table Talk", "Lectures on Shakespeare"),
            correctIndex = 1,
            explanation = "Coleridge makes this celebrated distinction at the end of Chapter XIII of 'Biographia Literaria' (1817)."
        ),
        QuizQuestion(
            id = "m_u1_11", unitId = "unit_1",
            prompt = "Which novel by Mary Shelley carries the subtitle 'The Modern Prometheus'?",
            options = listOf("The Last Man", "Frankenstein", "Valperga", "Matilda"),
            correctIndex = 1,
            explanation = "Mary Shelley's 'Frankenstein; or, The Modern Prometheus' was published anonymously in 1818."
        ),
        QuizQuestion(
            id = "m_u1_12", unitId = "unit_1",
            prompt = "Which Brontë novel uses the nested narrators Lockwood and Nelly Dean?",
            options = listOf("Jane Eyre", "Villette", "Wuthering Heights", "Agnes Grey"),
            correctIndex = 2,
            explanation = "Emily Brontë's 'Wuthering Heights' (1847) frames Heathcliff and Catherine's story through Mr. Lockwood and Ellen (Nelly) Dean."
        ),
        QuizQuestion(
            id = "m_u1_13", unitId = "unit_1",
            prompt = "George Eliot's 'Middlemarch' (1871–72) carries which subtitle?",
            options = listOf("A Tale of Two Cities", "A Study of Provincial Life", "A Novel Without a Hero", "A Pure Woman"),
            correctIndex = 1,
            explanation = "'Middlemarch: A Study of Provincial Life' follows Dorothea Brooke and Tertius Lydgate."
        ),
        QuizQuestion(
            id = "m_u1_14", unitId = "unit_1",
            prompt = "Which two anthropological/mythic studies did T.S. Eliot credit in the Notes to 'The Waste Land'?",
            options = listOf(
                "Frazer's 'The Golden Bough' and Jessie Weston's 'From Ritual to Romance'",
                "Tylor's 'Primitive Culture' and Levi-Strauss's 'Mythologiques'",
                "Campbell's 'Hero with a Thousand Faces' and Frye's 'Anatomy of Criticism'",
                "Eliade's 'Sacred and Profane' and Propp's 'Morphology'"
            ),
            correctIndex = 0,
            explanation = "Eliot explicitly cited Jessie L. Weston's 'From Ritual to Romance' and Sir James Frazer's 'The Golden Bough'."
        ),
        QuizQuestion(
            id = "m_u1_15", unitId = "unit_1",
            prompt = "Which 1956 play by John Osborne inaugurated the 'Angry Young Men' / kitchen-sink movement on the British stage?",
            options = listOf("The Caretaker", "Look Back in Anger", "Roots", "A Taste of Honey"),
            correctIndex = 1,
            explanation = "John Osborne's 'Look Back in Anger' (1956), featuring Jimmy Porter, transformed postwar British theatre."
        ),
        QuizQuestion(
            id = "m_u1_16", unitId = "unit_1",
            prompt = "Which critic coined the term 'historiographic metafiction' for postmodern novels like 'Midnight's Children' and 'The French Lieutenant's Woman'?",
            options = listOf("Fredric Jameson", "Linda Hutcheon", "Jean-François Lyotard", "Brian McHale"),
            correctIndex = 1,
            explanation = "Linda Hutcheon coined 'historiographic metafiction' in 'A Poetics of Postmodernism' (1988)."
        ),

        // UNIT 2 (12 questions)
        QuizQuestion(
            id = "m_u2_01", unitId = "unit_2",
            prompt = "Which text published in 1794 is recognized as the first book written in English by an Indian author?",
            options = listOf("Rajmohan's Wife", "The Travels of Dean Mahomet", "Bianca", "The Fakeer of Jungheera"),
            correctIndex = 1,
            explanation = "Sake Dean Mahomet's epistolary travelogue 'The Travels of Dean Mahomet' was published in Cork in 1794."
        ),
        QuizQuestion(
            id = "m_u2_02", unitId = "unit_2",
            prompt = "Who serialized 'Rajmohan's Wife' (1864), the first Indian novel in English?",
            options = listOf("Michael Madhusudan Dutt", "Bankim Chandra Chatterjee", "Lal Behari Day", "Toru Dutt"),
            correctIndex = 1,
            explanation = "Bankim Chandra Chatterjee serialized 'Rajmohan's Wife' in 'The Indian Field' in 1864."
        ),
        QuizQuestion(
            id = "m_u2_03", unitId = "unit_2",
            prompt = "In which year did Rabindranath Tagore win the Nobel Prize in Literature?",
            options = listOf("1910", "1913", "1917", "1921"),
            correctIndex = 1,
            explanation = "Tagore received the Nobel Prize in Literature in 1913 following the English 'Gitanjali' (1912)."
        ),
        QuizQuestion(
            id = "m_u2_04", unitId = "unit_2",
            prompt = "R.K. Narayan's fictional South Indian town of Malgudi first appeared in which 1935 novel?",
            options = listOf("The Bachelor of Arts", "The English Teacher", "Swami and Friends", "The Guide"),
            correctIndex = 2,
            explanation = "Malgudi debuted in R.K. Narayan's first novel, 'Swami and Friends' (1935)."
        ),
        QuizQuestion(
            id = "m_u2_05", unitId = "unit_2",
            prompt = "Who founded the Writers Workshop publishing house in Calcutta in 1958?",
            options = listOf("Nissim Ezekiel", "P. Lal", "Dom Moraes", "R. Parthasarathy"),
            correctIndex = 1,
            explanation = "Purushottama Lal (P. Lal) founded Writers Workshop in 1958, nurturing post-independence Indian English poetry."
        ),
        QuizQuestion(
            id = "m_u2_06", unitId = "unit_2",
            prompt = "Kamala Das's famous lines 'I speak three languages, write in / Two, dream in one' appear in which poem?",
            options = listOf("The Looking Glass", "My Grandmother's House", "An Introduction", "The Freaks"),
            correctIndex = 2,
            explanation = "'An Introduction' from 'Summer in Calcutta' (1965) defends her choice to write in English."
        ),
        QuizQuestion(
            id = "m_u2_07", unitId = "unit_2",
            prompt = "Girish Karnad's play 'Hayavadana' draws its central plot from Thomas Mann's 'The Transposed Heads' and which Sanskrit collection?",
            options = listOf("Panchatantra", "Kathasaritsagara (Vetala Panchavimshati)", "Hitopadesha", "Natyashastra"),
            correctIndex = 1,
            explanation = "Karnad drew from the Vetala Panchavimshati tales in Somadeva's 'Kathasaritsagara' via Thomas Mann's novella."
        ),
        QuizQuestion(
            id = "m_u2_08", unitId = "unit_2",
            prompt = "Which trio of novels forms Amitav Ghosh's 'Ibis Trilogy'?",
            options = listOf(
                "The Circle of Reason, The Shadow Lines, The Glass Palace",
                "Sea of Poppies, River of Smoke, Flood of Fire",
                "The Hungry Tide, Gun Island, Jungle Nama",
                "The Calcutta Chromosome, In an Antique Land, Countdown"
            ),
            correctIndex = 1,
            explanation = "The Ibis Trilogy chronicles the 19th-century opium trade through 'Sea of Poppies' (2008), 'River of Smoke' (2011), and 'Flood of Fire' (2015)."
        ),
        QuizQuestion(
            id = "m_u2_09", unitId = "unit_2",
            prompt = "Which character serves as the twin protagonists alongside Estha in Arundhati Roy's 'The God of Small Things'?",
            options = listOf("Ammu", "Rahel", "Sophie Mol", "Baby Kochamma"),
            correctIndex = 1,
            explanation = "Fraternal twins Rahel and Esthappen (Estha) anchor 'The God of Small Things' (1997)."
        ),
        QuizQuestion(
            id = "m_u2_10", unitId = "unit_2",
            prompt = "Who wrote the pioneering Tamil Dalit autobiography 'Karukku' (1992)?",
            options = listOf("Sivakami", "Bama", "Imayam", "Cho. Dharman"),
            correctIndex = 1,
            explanation = "Bama (Faustina Mary Fatima Rani) authored 'Karukku' (1992), translated into English by Lakshmi Holmström."
        ),
        QuizQuestion(
            id = "m_u2_11", unitId = "unit_2",
            prompt = "Who won the Pulitzer Prize for Fiction in 2000 for the short-story collection 'Interpreter of Maladies'?",
            options = listOf("Bharati Mukherjee", "Chitra Banerjee Divakaruni", "Jhumpa Lahiri", "Kiran Desai"),
            correctIndex = 2,
            explanation = "Jhumpa Lahiri won the 2000 Pulitzer Prize for Fiction for her debut collection 'Interpreter of Maladies'."
        ),
        QuizQuestion(
            id = "m_u2_12", unitId = "unit_2",
            prompt = "Nissim Ezekiel's 'Goodbye Party for Miss Pushpa T.S.' is celebrated for satirizing:",
            options = listOf("Victorian prosody", "Indianized English bureaucratic speech", "Rural folklore", "Classical Sanskrit drama"),
            correctIndex = 1,
            explanation = "Written as a dramatic monologue, the poem affectionately parodies 'Babu English' present-continuous idioms."
        ),

        // UNIT 3 (10 questions)
        QuizQuestion(
            id = "m_u3_01", unitId = "unit_3",
            prompt = "John Keats defined 'Negative Capability' as being capable of remaining in:",
            options = listOf(
                "Strict classical restraint",
                "Uncertainties, mysteries, doubts, without irritable reaching after fact and reason",
                "Continuous moral didacticism",
                "Subjective autobiographical confession"
            ),
            correctIndex = 1,
            explanation = "Keats wrote this definition in an 1817 letter praising Shakespeare's imaginative openness."
        ),
        QuizQuestion(
            id = "m_u3_02", unitId = "unit_3",
            prompt = "Who formulated the 'Objective Correlative' in his 1919 essay 'Hamlet and His Problems'?",
            options = listOf("Ezra Pound", "T.S. Eliot", "W.H. Auden", "F.R. Leavis"),
            correctIndex = 1,
            explanation = "T.S. Eliot argued Hamlet lacked an adequate 'objective correlative'—a set of objects or events evoking the emotion."
        ),
        QuizQuestion(
            id = "m_u3_03", unitId = "unit_3",
            prompt = "Evaluating a poem based on the emotional effect it produces in the reader is termed by Wimsatt and Beardsley as:",
            options = listOf("Intentional Fallacy", "Affective Fallacy", "Pathetic Fallacy", "Biographical Fallacy"),
            correctIndex = 1,
            explanation = "Wimsatt and Beardsley paired 'The Intentional Fallacy' (1946) with 'The Affective Fallacy' (1949) in 'The Verbal Icon'."
        ),
        QuizQuestion(
            id = "m_u3_04", unitId = "unit_3",
            prompt = "An 'Alexandrine' is a verse line consisting of:",
            options = listOf("Ten syllables in trochaic meter", "Twelve syllables in iambic hexameter", "Fourteen syllables in fourteeners", "Eight syllables in iambic tetrameter"),
            correctIndex = 1,
            explanation = "An Alexandrine is a line of six iambic feet (iambic hexameter), used at the end of a Spenserian stanza."
        ),
        QuizQuestion(
            id = "m_u3_05", unitId = "unit_3",
            prompt = "Which poetic device refers to a vivid verbal description of a visual work of art (such as Keats's Grecian Urn)?",
            options = listOf("Anaphora", "Ekphrasis", "Apostrophe", "Enjambment"),
            correctIndex = 1,
            explanation = "Ekphrasis is a literary description of or commentary on a visual work of art."
        ),
        QuizQuestion(
            id = "m_u3_06", unitId = "unit_3",
            prompt = "Which rhyme scheme characterizes a Shakespearean (English) sonnet?",
            options = listOf("ABBAABBA CDECDE", "ABAB BCBC CDCD EE", "ABAB CDCD EFEF GG", "AABB CCDD EEFF GG"),
            correctIndex = 2,
            explanation = "The Shakespearean sonnet consists of three quatrains and a closing couplet rhyming ABAB CDCD EFEF GG."
        ),
        QuizQuestion(
            id = "m_u3_07", unitId = "unit_3",
            prompt = "Pope's line 'Or stain her honour, or her new brocade' exemplifies which rhetorical figure?",
            options = listOf("Chiasmus", "Zeugma", "Litotes", "Oxymoron"),
            correctIndex = 1,
            explanation = "Zeugma yokes one verb ('stain') to two disparate objects ('honour' abstractly and 'brocade' literally)."
        ),
        QuizQuestion(
            id = "m_u3_08", unitId = "unit_3",
            prompt = "Cleanth Brooks warns critics against reducing a poem to a prose summary in his concept called:",
            options = listOf("Heresy of Paraphrase", "Tension in Poetry", "Seven Types of Ambiguity", "Close Reading"),
            correctIndex = 0,
            explanation = "In 'The Well Wrought Urn' (1947), Cleanth Brooks argues against the 'Heresy of Paraphrase'."
        ),
        QuizQuestion(
            id = "m_u3_09", unitId = "unit_3",
            prompt = "Which stanza form uses three-line interlocking rhymes (ABA BCB CDC DED) popularized by Dante?",
            options = listOf("Ottava Rima", "Rhyme Royal", "Terza Rima", "Heroic Quatrain"),
            correctIndex = 2,
            explanation = "Terza Rima uses interlocking tercets (ABA BCB CDC), notably employed by Shelley in 'Ode to the West Wind'."
        ),
        QuizQuestion(
            id = "m_u3_10", unitId = "unit_3",
            prompt = "When a grammatical sentence or clause runs past the end of a poetic line without terminal punctuation, it is called:",
            options = listOf("Caesura", "Enjambment", "Canto", "Elision"),
            correctIndex = 1,
            explanation = "Enjambment (run-on line) carries syntax seamlessly across a line break without pause."
        )
    )
}
