package com.example.data

import com.example.model.StudyNote
import com.example.model.SyllabusUnit

object CurriculumNotesBank {

    val units: List<SyllabusUnit> = listOf(
        SyllabusUnit(
            id = "unit_1",
            number = 1,
            title = "History of English & Indian Literatures",
            shortTitle = "Unit 1 • Literary History",
            subtitle = "Major authors, landmark texts & literary movements from Chaucer to Postmodernism",
            keyTopics = listOf("Old & Middle English", "Renaissance & Metaphysicals", "Augustan & Romantic", "Victorian & Modernism")
        ),
        SyllabusUnit(
            id = "unit_2",
            number = 2,
            title = "Indian Writing in English",
            shortTitle = "Unit 2 • Indian Writing",
            subtitle = "Pioneers, Gandhian trio, post-independence fiction, poetry, drama & translations",
            keyTopics = listOf("19th-Century Pioneers", "Rao, Anand, Narayan", "Modernist Poets", "Rushdie, Ghosh, Roy & Drama")
        ),
        SyllabusUnit(
            id = "unit_3",
            number = 3,
            title = "Literary Terms",
            shortTitle = "Unit 3 • Literary Terms",
            subtitle = "Key critical vocabulary, prosody, figures of speech & modernist concepts",
            keyTopics = listOf("Dissociation of Sensibility", "Intentional Fallacy", "Negative Capability", "Figures of Speech & Meter")
        ),
        SyllabusUnit(
            id = "unit_4",
            number = 4,
            title = "Literary Genres",
            shortTitle = "Unit 4 • Literary Genres",
            subtitle = "Fiction, non-fiction, life writings, diary, drama, essay, poetry & speculative forms",
            keyTopics = listOf("Epistolary & Picaresque Novel", "Theatre of the Absurd & Epic Theatre", "Elegy, Ode, Dramatic Monologue", "Life Writing & Sci-Fi")
        ),
        SyllabusUnit(
            id = "unit_5",
            number = 5,
            title = "Comparative Literature & Translation Studies",
            shortTitle = "Unit 5 • Comp Lit & Translation",
            subtitle = "World literature frameworks, equivalence, domestication vs foreignization & Indian schools",
            keyTopics = listOf("French vs American Schools", "Nida & Venuti", "Bassnett & Polysystem Theory", "Indian Translation Traditions")
        ),
        SyllabusUnit(
            id = "unit_6",
            number = 6,
            title = "Literary Criticism & Theory",
            shortTitle = "Unit 6 • Criticism & Theory",
            subtitle = "Classical poetics through Marxism, Structuralism, Post-structuralism, Feminism & Postcolonialism",
            keyTopics = listOf("Aristotle, Longinus, Arnold", "New Criticism & Russian Formalism", "Derrida, Foucault, Barthes", "Said, Spivak, Bhabha & Eco-criticism")
        ),
        SyllabusUnit(
            id = "unit_7",
            number = 7,
            title = "Current Literary Trends, Events & Awards",
            shortTitle = "Unit 7 • Trends & Awards",
            subtitle = "Booker, Nobel, Jnanpith, Sahitya Akademi, Pulitzer & contemporary digital/climate writing",
            keyTopics = listOf("Nobel & Booker Laureates", "Sahitya Akademi & Jnanpith", "Cli-Fi & Graphic Narratives", "Contemporary Global Voices")
        )
    )

    val notes: List<StudyNote> = listOf(
        // UNIT 1
        StudyNote(
            id = "note_u1_renaissance",
            unitId = "unit_1",
            title = "Chaucer to the English Renaissance & Metaphysicals",
            eraOrTag = "1340–1660",
            summary = "Traces the birth of vernacular English poetry with Geoffrey Chaucer through Elizabethan drama, Spencerian allegory, and the intellectual wit of John Donne and the Metaphysical school.",
            keyAuthors = listOf("Geoffrey Chaucer", "Edmund Spenser", "Christopher Marlowe", "William Shakespeare", "John Donne", "Andrew Marvell", "John Milton"),
            keyTexts = listOf(
                "The Canterbury Tales (c. 1387–1400)",
                "The Faerie Queene (1590, 1596)",
                "Doctor Faustus (1604)",
                "Songs and Sonnets (1633)",
                "Paradise Lost (1667)"
            ),
            highYieldPoints = listOf(
                "Chaucer's 'The Canterbury Tales' uses heroic couplets and opens in April at the Tabard Inn, Southwark.",
                "Tottel's Miscellany (1557) introduced the Petrarchan sonnet to English print via Wyatt and Surrey; Surrey pioneered blank verse.",
                "University Wits: Marlowe, Greene, Nashe, Lyly, Peele, Lodge, and Kyd (often grouped alongside them).",
                "Samuel Johnson coined the term 'Metaphysical Poets' in 'Lives of the Most Eminent English Poets' (1779–81), noting 'heterogeneous ideas yoked by violence together'.",
                "Milton's 'Areopagitica' (1644) is a prose defence against pre-publication censorship."
            ),
            readMinutes = 6
        ),
        StudyNote(
            id = "note_u1_augustan_romantic",
            unitId = "unit_1",
            title = "Restoration, Augustan Satire & the Romantic Revival",
            eraOrTag = "1660–1832",
            summary = "Covers neoclassical order, mock-epic satire, the rise of the English novel, and the Romantic shift toward imagination, nature, and common speech initiated in 1798.",
            keyAuthors = listOf("John Dryden", "Alexander Pope", "Jonathan Swift", "Daniel Defoe", "William Wordsworth", "S.T. Coleridge", "John Keats", "P.B. Shelley", "Mary Shelley"),
            keyTexts = listOf(
                "Mac Flecknoe (1682)",
                "The Rape of the Lock (1712/1714)",
                "Gulliver's Travels (1726)",
                "Lyrical Ballads (1798)",
                "Biographia Literaria (1817)",
                "Frankenstein (1818)"
            ),
            highYieldPoints = listOf(
                "Jeremy Collier's 'A Short View of the Immorality and Profaneness of the English Stage' (1698) attacked Restoration Comedy of Manners (Congreve, Wycherley).",
                "Ian Watt's 'The Rise of the Novel' (1957) links Defoe, Richardson ('Pamela'), and Fielding ('Tom Jones') to formal realism.",
                "Wordsworth added the famous Preface to the 1800 second edition of 'Lyrical Ballads' ('spontaneous overflow of powerful feelings').",
                "Coleridge distinguished Fancy from Primary and Secondary Imagination in Chapter XIII of 'Biographia Literaria'.",
                "Keats formulated 'Negative Capability' in an 1817 letter to his brothers George and Tom."
            ),
            readMinutes = 6
        ),
        StudyNote(
            id = "note_u1_victorian_modern",
            unitId = "unit_1",
            title = "Victorian Age, Modernism & Postmodern Shifts",
            eraOrTag = "1837–Present",
            summary = "Examines industrial realism, crisis of faith, Pre-Raphaelites, High Modernist fragmentation, Angry Young Men, and Postmodern metafiction.",
            keyAuthors = listOf("Charles Dickens", "George Eliot", "Alfred Tennyson", "Robert Browning", "T.S. Eliot", "Virginia Woolf", "James Joyce", "Samuel Beckett", "John Osborne", "Salman Rushdie"),
            keyTexts = listOf(
                "In Memoriam A.H.H. (1850)",
                "Middlemarch (1871–72)",
                "The Waste Land (1922)",
                "Ulysses (1922)",
                "Mrs Dalloway (1925)",
                "Waiting for Godot (1953/1955)"
            ),
            highYieldPoints = listOf(
                "1922 is the annus mirabilis of Modernism: 'The Waste Land' (Eliot), 'Ulysses' (Joyce), and 'Jacob's Room' (Woolf).",
                "Ferdinand de Saussure, Freud, Frazer ('The Golden Bough'), and Jessie Weston ('From Ritual to Romance') deeply shaped Modernist texts.",
                "Movement Poets (1950s): Philip Larkin, Kingsley Amis, Thom Gunn, Elizabeth Jennings—anti-romantic, ironic, formal.",
                "Postmodern traits: historiographic metafiction (Linda Hutcheon), pastiche, unreliable narration, intertextuality (Fowles, Eco, Carter, Barnes)."
            ),
            readMinutes = 7
        ),

        // UNIT 2
        StudyNote(
            id = "note_u2_pioneers",
            unitId = "unit_2",
            title = "19th-Century Pioneers & the Gandhian Trio",
            eraOrTag = "IWE Foundations",
            summary = "Follows early Indian English prose and verse from Dean Mahomet and Toru Dutt to the foundational 1930s novelists Mulk Raj Anand, R.K. Narayan, and Raja Rao.",
            keyAuthors = listOf("Dean Mahomet", "Henry Derozio", "Toru Dutt", "Bankim Chandra Chatterjee", "Rabindranath Tagore", "Mulk Raj Anand", "R.K. Narayan", "Raja Rao"),
            keyTexts = listOf(
                "Travels of Dean Mahomet (1794)",
                "Rajmohan's Wife (1864)",
                "Ancient Ballads and Legends of Hindustan (1882)",
                "Gitanjali (1912 English)",
                "Untouchable (1935)",
                "Swami and Friends (1935)",
                "Kanthapura (1938)"
            ),
            highYieldPoints = listOf(
                "Sake Dean Mahomet wrote the first book in English by an Indian ('The Travels of Dean Mahomet', 1794).",
                "Bankim Chandra's 'Rajmohan's Wife' (1864) is widely recognized as the first Indian novel in English.",
                "Raja Rao's Foreword to 'Kanthapura' (1938) is the canonical manifesto on 'Indianizing' English cadence and rhythm.",
                "Mulk Raj Anand's 'Untouchable' (1935) depicts a single day in the life of Bakha, carrying a preface by E.M. Forster.",
                "Tagore won the Nobel Prize in Literature in 1913 for 'Gitanjali' (Introduction by W.B. Yeats)."
            ),
            readMinutes = 5
        ),
        StudyNote(
            id = "note_u2_modern_poetry_drama",
            unitId = "unit_2",
            title = "Post-Independence Poetry, Drama & Women's Voices",
            eraOrTag = "1947–1990s",
            summary = "Covers Nissim Ezekiel and the Bombay Poets, confessional verse of Kamala Das, Indian English drama, and psychological realism in women's fiction.",
            keyAuthors = listOf("Nissim Ezekiel", "Kamala Das", "A.K. Ramanujan", "Jayanta Mahapatra", "Girish Karnad", "Mahesh Dattani", "Anita Desai", "Shashi Deshpande"),
            keyTexts = listOf(
                "A Time to Change (1952)",
                "Summer in Calcutta (1965)",
                "Relationship (1980)",
                "Tughlaq (1964)",
                "Final Solutions (1993)",
                "Clear Light of Day (1980)",
                "That Long Silence (1988)"
            ),
            highYieldPoints = listOf(
                "Nissim Ezekiel edited 'Quest' and mentored Dom Moraes, Adil Jussawalla, and Eunice de Souza.",
                "Jayanta Mahapatra became the first Indian English poet to win the Sahitya Akademi Award (1981) for 'Relationship'.",
                "Girish Karnad's 'Hayavadana' adapts Thomas Mann's 'The Transposed Heads' and traditional Yakshagana folk theatre.",
                "Mahesh Dattani's 'Final Solutions' (1993) was the first English-language play to win the Sahitya Akademi Award (1998).",
                "P. Lal founded the Writers Workshop in Calcutta (1958)."
            ),
            readMinutes = 6
        ),
        StudyNote(
            id = "note_u2_contemporary_iwe",
            unitId = "unit_2",
            title = "Midnight's Children, Diaspora & Contemporary IWE",
            eraOrTag = "1981–Present",
            summary = "Explores magical realism, subaltern historiography, diaspora writing, and Dalit/regional texts in English translation.",
            keyAuthors = listOf("Salman Rushdie", "Amitav Ghosh", "Arundhati Roy", "Vikram Seth", "Jhumpa Lahiri", "Bama", "Omprakash Valmiki", "Perumal Murugan"),
            keyTexts = listOf(
                "Midnight's Children (1981)",
                "The Shadow Lines (1988)",
                "A Suitable Boy (1993)",
                "The God of Small Things (1997)",
                "Karukku (1992 / tr. 2000)",
                "Joothan (1997 / tr. 2003)"
            ),
            highYieldPoints = listOf(
                "'Midnight's Children' (1981) won the Booker Prize, Booker of Bookers (1993), and Best of the Booker (2008).",
                "Amitav Ghosh's 'The Ibis Trilogy' comprises 'Sea of Poppies', 'River of Smoke', and 'Flood of Fire'.",
                "Arundhati Roy won the 1997 Booker Prize for 'The God of Small Things', set in Ayemenem, Kerala.",
                "Dalit life writings in translation—Bama's 'Karukku' (translated by Lakshmi Holmström) and Valmiki's 'Joothan' (Arun Prabha Mukherjee)—are frequent LAQP01 questions."
            ),
            readMinutes = 5
        ),

        // UNIT 3
        StudyNote(
            id = "note_u3_critical_terms",
            unitId = "unit_3",
            title = "Signature Critical Concepts & Fallacies",
            eraOrTag = "LAQP01 Core",
            summary = "High-frequency literary terms coined by T.S. Eliot, Wimsatt & Beardsley, Keats, Ruskin, Matthew Arnold, and Cleanth Brooks.",
            keyAuthors = listOf("T.S. Eliot", "W.K. Wimsatt & Monroe Beardsley", "John Ruskin", "John Keats", "Cleanth Brooks", "I.A. Richards"),
            keyTexts = listOf(
                "The Metaphysical Poets (1921)",
                "Hamlet and His Problems (1919)",
                "The Verbal Icon (1954)",
                "Modern Painters (1856)",
                "The Well Wrought Urn (1947)"
            ),
            highYieldPoints = listOf(
                "Dissociation of Sensibility (T.S. Eliot): split between thought and feeling emerging in the 17th century after Donne.",
                "Objective Correlative (Eliot, 'Hamlet and His Problems'): a set of objects, situation, or chain of events serving as the formula for a particular emotion.",
                "Intentional Fallacy & Affective Fallacy (Wimsatt & Beardsley): judging a work by authorial intent or reader's emotional effect.",
                "Pathetic Fallacy (John Ruskin): attributing human emotions to inanimate nature (distinct from personification).",
                "Heresy of Paraphrase (Cleanth Brooks): prose paraphrase cannot capture a poem's organic meaning."
            ),
            readMinutes = 5
        ),
        StudyNote(
            id = "note_u3_poetic_forms",
            unitId = "unit_3",
            title = "Prosody, Verse Forms & Poetic Genres",
            eraOrTag = "Meter & Form",
            summary = "Distinguishes blank verse, heroic couplet, sonnet varieties, dramatic monologue, elegy, ballad, and ode structures.",
            keyAuthors = listOf("Petrarch", "Shakespeare", "Spenser", "Robert Browning", "Thomas Gray", "John Milton"),
            keyTexts = listOf(
                "My Last Duchess (1842)",
                "Lycidas (1637)",
                "Elegy Written in a Country Churchyard (1751)",
                "Ode to the West Wind (1820)"
            ),
            highYieldPoints = listOf(
                "Blank Verse = unrhymed iambic pentameter; Free Verse (vers libre) lacks both regular meter and rhyme.",
                "Sonnet rhyme schemes: Petrarchan (ABBAABBA + CDECDE/CDCDCD), Shakespearean (ABAB CDCD EFEF GG), Spenserian (ABAB BCBC CDCD EE).",
                "Terza Rima (ABA BCB CDC): used by Dante and Shelley in 'Ode to the West Wind'.",
                "Spenserian Stanza: 8 iambic pentameter lines + 1 Alexandrine (iambic hexameter), rhyming ABABBCBCC.",
                "Pastoral Elegy conventions: invocation, nature mourning, procession of mourners, consolation (e.g., 'Lycidas', 'Adonais', 'Thyrsis')."
            ),
            readMinutes = 5
        ),
        StudyNote(
            id = "note_u3_figures_speech",
            unitId = "unit_3",
            title = "Figures of Speech, Rhetoric & Narrative Devices",
            eraOrTag = "Rhetoric",
            summary = "Sharpens distinctions between easily confused tropes and devices tested in LAQP01 matching questions.",
            keyAuthors = listOf("Quintilian", "George Puttenham", "M.H. Abrams", "Gérard Genette"),
            keyTexts = listOf(
                "A Glossary of Literary Terms (M.H. Abrams)",
                "The Arte of English Poesie (1589)"
            ),
            highYieldPoints = listOf(
                "Metonymy substitutes a closely associated attribute ('the Crown' for monarchy); Synecdoche substitutes a part for the whole or whole for part ('all hands on deck').",
                "Zeugma yokes two nouns to one verb/adjective in different senses ('stained her honour, or her new brocade').",
                "Chiasmus reverses grammatical structure (ABBA: 'Ask not what your country can do for you...').",
                "Oxymoron places contradictory terms side by side ('bitter sweet'); Paradox is a seemingly self-contradictory statement revealing truth.",
                "Ekphrasis: vivid literary description of a visual work of art (e.g., Keats's 'Ode on a Grecian Urn')."
            ),
            readMinutes = 4
        ),

        // UNIT 4
        StudyNote(
            id = "note_u4_novel_subgenres",
            unitId = "unit_4",
            title = "Novel Subgenres: Epistolary, Picaresque, Bildungsroman & Gothic",
            eraOrTag = "Prose Fiction",
            summary = "Maps major fictional forms and their inaugural texts in European, British, and postcolonial traditions.",
            keyAuthors = listOf("Samuel Richardson", "Henry Fielding", "Tobias Smollett", "Johann Wolfgang von Goethe", "Horace Walpole", "Ann Radcliffe"),
            keyTexts = listOf(
                "Pamela (1740) & Clarissa (1748)",
                "Tom Jones (1749)",
                "Wilhelm Meister's Apprenticeship (1795)",
                "The Castle of Otranto (1764)",
                "A Portrait of the Artist as a Young Man (1916)"
            ),
            highYieldPoints = listOf(
                "Picaresque novel: episodic adventures of a roguish picaro (e.g., 'Lazarillo de Tormes', 'Moll Flanders', 'Joseph Andrews').",
                "Bildungsroman = novel of formation/coming-of-age; Künstlerroman = subset tracing an artist's growth (e.g., Joyce's 'Portrait').",
                "First English Gothic novel: Horace Walpole's 'The Castle of Otranto' (1764).",
                "Epistolary novel tells its story via letters ('Pamela', 'Frankenstein' frame, 'The Color Purple').",
                "Roman à clef: real historical figures disguised under fictional names (e.g., Huxley's 'Point Counter Point')."
            ),
            readMinutes = 5
        ),
        StudyNote(
            id = "note_u4_drama_modes",
            unitId = "unit_4",
            title = "Drama Modes: Absurd, Epic Theatre, Kitchen Sink & Problem Plays",
            eraOrTag = "Stage & Form",
            summary = "Contrasts Aristotelian catharsis with Brechtian alienation, Martin Esslin's Theatre of the Absurd, and 1950s British realism.",
            keyAuthors = listOf("Henrik Ibsen", "G.B. Shaw", "Bertolt Brecht", "Samuel Beckett", "Harold Pinter", "John Osborne", "Antonin Artaud"),
            keyTexts = listOf(
                "A Doll's House (1879)",
                "Mother Courage and Her Children (1941)",
                "The Theatre of the Absurd (Martin Esslin, 1961)",
                "The Birthday Party (1957)",
                "Look Back in Anger (1956)"
            ),
            highYieldPoints = listOf(
                "Bertolt Brecht's Epic Theatre uses Verfremdungseffekt (alienation/estrangement effect) to prevent passive emotional immersion.",
                "Martin Esslin coined 'Theatre of the Absurd' (1961) grouping Beckett, Ionesco, Genet, Adamov, and early Pinter.",
                "Antonin Artaud proposed 'Theatre of Cruelty' in 'The Theatre and Its Double' (1938).",
                "Comedy of Humours (Ben Jonson) vs Comedy of Manners (Restoration) vs Sentimental Comedy (Steele, Goldsmith's reaction)."
            ),
            readMinutes = 5
        ),
        StudyNote(
            id = "note_u4_nonfiction_speculative",
            unitId = "unit_4",
            title = "Life Writing, Essays, Travelogues & Speculative Fiction",
            eraOrTag = "Non-Fiction & Sci-Fi",
            summary = "Reviews biography, autobiography, memoir, diary, periodical essays, and the evolution of science fiction and dystopias.",
            keyAuthors = listOf("Michel de Montaigne", "Francis Bacon", "Samuel Pepys", "James Boswell", "Mary Wollstonecraft", "H.G. Wells", "Ursula K. Le Guin"),
            keyTexts = listOf(
                "Essais (Montaigne, 1580)",
                "The Tatler (1709) & The Spectator (1711)",
                "Life of Samuel Johnson (1791)",
                "The Time Machine (1895)",
                "The Left Hand of Darkness (1969)"
            ),
            highYieldPoints = listOf(
                "Montaigne coined 'Essais' ('attempts'); Francis Bacon introduced the aphoristic essay into English (1597).",
                "Samuel Pepys's Diary (1660–1669) provides an eyewitness record of the Restoration, Plague, and Great Fire of London.",
                "Memoir focuses on specific public/personal episodes, whereas autobiography spans a full life narrative; biography is written by another.",
                "Darko Suvin defined science fiction through 'cognitive estrangement' and the 'novum'."
            ),
            readMinutes = 4
        ),

        // UNIT 5
        StudyNote(
            id = "note_u5_comp_lit",
            unitId = "unit_5",
            title = "Comparative Literature: Schools, World Literature & Concepts",
            eraOrTag = "Comp Lit",
            summary = "Covers Goethe's Weltliteratur, French influence studies vs American interdisciplinary schools, and Indian comparative aesthetics.",
            keyAuthors = listOf("J.W. von Goethe", "René Wellek", "Susan Bassnett", "Franco Moretti", "David Damrosch", "Sisir Kumar Das", "Amiya Dev"),
            keyTexts = listOf(
                "The Crisis of Comparative Literature (Wellek, 1958)",
                "Comparative Literature: A Critical Introduction (Bassnett, 1993)",
                "Conjectures on World Literature (Moretti, 2000)",
                "What Is World Literature? (Damrosch, 2003)"
            ),
            highYieldPoints = listOf(
                "Goethe popularized 'Weltliteratur' (World Literature) in conversations with Eckermann (1827).",
                "French School (Van Tieghem, Guyard) focused on binary influence/reception; American School (Remak, Wellek) expanded to interdisciplinary arts/theory.",
                "Franco Moretti proposed 'distant reading' vs close reading; Gayatri Spivak called for 'Death of a Discipline' (2003) advocating planetarity.",
                "Jadavpur University established India's first Department of Comparative Literature in 1956 (Buddhadeva Bose)."
            ),
            readMinutes = 5
        ),
        StudyNote(
            id = "note_u5_translation_theory",
            unitId = "unit_5",
            title = "Translation Studies: Key Theorists & Turns",
            eraOrTag = "Translation",
            summary = "Outlines Dryden's triad, Jakobson's three types, Nida's equivalence, polysystem theory, Skopos theory, and Venuti's ethics.",
            keyAuthors = listOf("John Dryden", "Roman Jakobson", "Eugene Nida", "Itamar Even-Zohar", "Lawrence Venuti", "Sujit Mukherjee", "Tejaswini Niranjana"),
            keyTexts = listOf(
                "On Linguistic Aspects of Translation (Jakobson, 1959)",
                "Toward a Science of Translating (Nida, 1964)",
                "The Translator's Invisibility (Venuti, 1995)",
                "Translation as Discovery (Sujit Mukherjee, 1981)"
            ),
            highYieldPoints = listOf(
                "Dryden's Preface to Ovid's Epistles (1680): Metaphrase (word-for-word), Paraphrase (sense-for-sense), Imitation (free adaptation).",
                "Jakobson's 3 types: Intralingual (rewording), Interlingual (translation proper), Intersemiotic (transmutation across sign systems).",
                "Eugene Nida: Formal Equivalence (structure-focused) vs Dynamic/Functional Equivalence (reader-response focused).",
                "Lawrence Venuti: Domestication (fluent, erasing foreignness) vs Foreignization (preserving source culture difference).",
                "André Lefevere & Susan Bassnett spearheaded the 'Cultural Turn' in Translation Studies (1990)."
            ),
            readMinutes = 6
        ),
        StudyNote(
            id = "note_u5_indian_translation",
            unitId = "unit_5",
            title = "Translation in India & Postcolonial Retellings",
            eraOrTag = "Indian Context",
            summary = "Reviews Indian concepts of Anuvad, Bhashantara, Chhaya, and major literary translations shaping CUET PG LAQP01.",
            keyAuthors = listOf("A.K. Ramanujan", "Gayatri Chakravorty Spivak", "Harish Trivedi", "Rita Kothari", "Arshia Sattar", "Bibek Debroy"),
            keyTexts = listOf(
                "Is There an Indian Way of Thinking? (Ramanujan)",
                "The Politics of Translation (Spivak, 1993)",
                "Post-colonial Translation: Theory and Practice (Bassnett & Trivedi, 1999)",
                "Translating India (Rita Kothari, 2003)"
            ),
            highYieldPoints = listOf(
                "A.K. Ramanujan translated classical Tamil Sangam poetry ('The Interior Landscape') and Kannada Vacanas ('Speaking of Siva').",
                "Gayatri Spivak translated Mahasweta Devi's 'Draupadi', 'Breast Stories', and 'Imaginary Maps'.",
                "Sujit Mukherjee characterized Indian translation practice as 'translation as new writing' or transcreation (P. Lal).",
                "Tejaswini Niranjana ('Siting Translation', 1992) critiqued colonial translations for fixing colonized subjects in inferior stereotypes."
            ),
            readMinutes = 4
        ),

        // UNIT 6
        StudyNote(
            id = "note_u6_classical_to_new",
            unitId = "unit_6",
            title = "Classical Poetics, Arnold & New Criticism",
            eraOrTag = "Criticism",
            summary = "From Plato and Aristotle's mimetic debates through Longinus, Sidney, Dryden, Johnson, Arnold, and Anglo-American New Criticism.",
            keyAuthors = listOf("Plato", "Aristotle", "Longinus", "Philip Sidney", "Matthew Arnold", "I.A. Richards", "Cleanth Brooks"),
            keyTexts = listOf(
                "Poetics (Aristotle)",
                "On the Sublime (Longinus)",
                "An Apology for Poetry (1595)",
                "The Study of Poetry (1880)",
                "Practical Criticism (1929)"
            ),
            highYieldPoints = listOf(
                "Aristotle's 6 elements of tragedy (ordered): Plot (mythos), Character (ethos), Thought (dianoia), Diction (lexis), Song (melos), Spectacle (opsis).",
                "Hamartia (tragic error), Anagnorisis (recognition), Peripeteia (reversal), Catharsis (purgation/clarification).",
                "Longinus identifies 5 sources of the Sublime: grandeur of thought, vigorous passion, figures, noble diction, dignified composition.",
                "Matthew Arnold introduced the 'Touchstone Method' and defined criticism as 'disinterested endeavor to learn and propagate the best'."
            ),
            readMinutes = 6
        ),
        StudyNote(
            id = "note_u6_formalism_poststructuralism",
            unitId = "unit_6",
            title = "Russian Formalism, Marxism, Structuralism & Deconstruction",
            eraOrTag = "20th-Century Theory",
            summary = "Tracks the linguistic turn from Saussure and Bakhtin to Althusser, Barthes, Foucault, and Derrida.",
            keyAuthors = listOf("Viktor Shklovsky", "Mikhail Bakhtin", "Raymond Williams", "Terry Eagleton", "Roland Barthes", "Jacques Derrida", "Michel Foucault"),
            keyTexts = listOf(
                "Art as Technique (Shklovsky, 1917)",
                "Course in General Linguistics (Saussure, 1916)",
                "The Death of the Author (Barthes, 1967)",
                "Of Grammatology (Derrida, 1967)",
                "Ideology and Ideological State Apparatuses (Althusser, 1970)"
            ),
            highYieldPoints = listOf(
                "Shklovsky: 'Ostranenie' (defamiliarization) makes the familiar strange; Fabula (story) vs Sjuzhet (plot).",
                "Bakhtin: Dialogism, Heteroglossia, Polyphony, and Carnivalesque ('Rabelais and His World').",
                "Antonio Gramsci coined 'Cultural Hegemony'; Louis Althusser introduced 'Interpellation' and ISA/RSA.",
                "Derrida critiques 'Logocentrism' and 'Metaphysics of Presence', introducing 'Différance' (differ + defer) and 'Trace'."
            ),
            readMinutes = 7
        ),
        StudyNote(
            id = "note_u6_feminism_postcolonial_eco",
            unitId = "unit_6",
            title = "Feminism, Postcolonialism & Ecocriticism",
            eraOrTag = "Contemporary Theory",
            summary = "Key interventions by Woolf, Beauvoir, Showalter, Butler, Fanon, Said, Spivak, Bhabha, and ecocritics.",
            keyAuthors = listOf("Simone de Beauvoir", "Elaine Showalter", "Judith Butler", "Frantz Fanon", "Edward Said", "Homi K. Bhabha", "Cheryll Glotfelty"),
            keyTexts = listOf(
                "The Second Sex (1949)",
                "A Literature of Their Own (1977)",
                "Gender Trouble (1990)",
                "Orientalism (1978)",
                "Can the Subaltern Speak? (1988)",
                "The Location of Culture (1994)",
                "The Ecocriticism Reader (1996)"
            ),
            highYieldPoints = listOf(
                "Elaine Showalter coined 'Gynocriticism' and divided women's writing into Feminine (1840–80), Feminist (1880–1920), and Female (1920–present) phases.",
                "Homi K. Bhabha's key concepts: Hybridity, Mimicry ('almost the same, but not quite'), Ambivalence, and Third Space.",
                "Cheryll Glotfelty co-edited 'The Ecocriticism Reader' (1996); Lawrence Buell and Greg Garrard are major ecocritical theorists; Vandana Shiva leads Indian ecofeminism.",
                "Raymond Williams ('The Country and the City', 1973) and Jonathan Bate ('Romantic Ecology') bridge Marxism and Ecocriticism."
            ),
            readMinutes = 6
        ),

        // UNIT 7
        StudyNote(
            id = "note_u7_global_awards",
            unitId = "unit_7",
            title = "Nobel, Booker & International Booker Laureates",
            eraOrTag = "Major Awards",
            summary = "High-yield reference of recent Nobel Prize, Booker Prize, and International Booker winners frequently tested in Unit 7.",
            keyAuthors = listOf("Han Kang", "Jon Fosse", "Annie Ernaux", "Abdulrazak Gurnah", "Samantha Harvey", "Jenny Erpenbeck", "Geetanjali Shree"),
            keyTexts = listOf(
                "The Vegetarian (Han Kang, Nobel 2024)",
                "Orbital (Samantha Harvey, Booker 2024)",
                "Kairos (Jenny Erpenbeck, Int'l Booker 2024)",
                "Tomb of Sand (Geetanjali Shree / tr. Daisy Rockwell, Int'l Booker 2022)",
                "Prophet Song (Paul Lynch, Booker 2023)"
            ),
            highYieldPoints = listOf(
                "Han Kang (South Korea) won the 2024 Nobel Prize in Literature ('intense poetic prose that confronts historical traumas').",
                "Jon Fosse (Norway) won the 2023 Nobel Prize; Annie Ernaux (France) won 2022; Abdulrazak Gurnah won 2021.",
                "Geetanjali Shree's 'Tomb of Sand' (Ret Samadhi), translated by Daisy Rockwell, was the first Hindi novel to win the International Booker Prize (2022).",
                "Samantha Harvey's 'Orbital' won the 2024 Booker Prize; Paul Lynch's 'Prophet Song' won 2023; Shehan Karunatilaka's 'The Seven Moons of Maali Almeida' won 2022."
            ),
            readMinutes = 5
        ),
        StudyNote(
            id = "note_u7_indian_awards",
            unitId = "unit_7",
            title = "Jnanpith, Sahitya Akademi & JCB Prize Highlights",
            eraOrTag = "Indian Literary Honours",
            summary = "Tracks India's premier literary awards including recent Jnanpith, Sahitya Akademi (English), and JCB Prize winners.",
            keyAuthors = listOf("Gulzar", "Jagadguru Rambhadracharya", "Damodar Mauzo", "Easterine Kire", "Neelum Saran Gour", "Anuradha Roy", "Perumal Murugan"),
            keyTexts = listOf(
                "Spirit Nights (Easterine Kire, Sahitya Akademi English 2024)",
                "Requiem in Raga Janki (Neelum Saran Gour, Sahitya Akademi English 2023)",
                "All the Lives We Never Lived (Anuradha Roy, Sahitya Akademi English 2022)",
                "Fire Bird (Perumal Murugan / tr. Janani Kannan, JCB 2023)"
            ),
            highYieldPoints = listOf(
                "58th Jnanpith Award (2023, announced 2024): awarded jointly to Urdu poet Gulzar and Sanskrit scholar Jagadguru Rambhadracharya.",
                "First Jnanpith Award (1965): G. Sankara Kurup (Malayalam); first English writer to win Jnanpith: Amitav Ghosh (54th, 2018).",
                "First Sahitya Akademi Award for English (1960): R.K. Narayan for 'The Guide'.",
                "2024 JCB Prize for Literature: Upamanyu Chatterjee for 'Lorenzo Searches for the Meaning of Life'."
            ),
            readMinutes = 5
        ),
        StudyNote(
            id = "note_u7_trends",
            unitId = "unit_7",
            title = "Contemporary Trends: Cli-Fi, Graphic Narratives & Digital Humanities",
            eraOrTag = "Current Trends",
            summary = "Covers climate fiction, Indian graphic novels, autofiction, digital humanities, and contemporary literary festivals.",
            keyAuthors = listOf("Amitav Ghosh", "Sarnath Banerjee", "Amruta Patil", "Art Spiegelman", "Marjane Satrapi", "Franco Moretti"),
            keyTexts = listOf(
                "The Great Derangement (Amitav Ghosh, 2016)",
                "Corridor (Sarnath Banerjee, 2004)",
                "Kari (Amruta Patil, 2008)",
                "Maus (Art Spiegelman)",
                "Persepolis (Marjane Satrapi)"
            ),
            highYieldPoints = listOf(
                "Dan Bloom coined 'Cli-Fi' (Climate Fiction); Amitav Ghosh's 'The Great Derangement' critiques realist fiction's failure to confront climate crisis.",
                "Sarnath Banerjee's 'Corridor' (2004) is widely regarded as India's first graphic novel; Orijit Sen's 'River of Stories' (1994) is an early precursor.",
                "Serge Doubrovsky coined 'Autofiction' (1977), prominent in contemporary works by Annie Ernaux, Karl Ove Knausgård, and Ben Lerner.",
                "Digital Humanities combines computational text mining, stylometry, and distant reading with literary archives."
            ),
            readMinutes = 4
        )
    )
}
