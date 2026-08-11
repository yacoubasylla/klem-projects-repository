from datetime import date

from klem_ref_bot.extraction import heuristics


def test_detect_titre_returns_first_non_empty_line():
    text = "\n\nCirculaire relative à l'importation\nPubliée le 15 janvier 2026\n"
    assert heuristics.detect_titre(text) == "Circulaire relative à l'importation"


def test_detect_titre_returns_empty_string_when_no_content():
    assert heuristics.detect_titre("\n\n   \n") == ""


def test_detect_type_matches_known_keyword():
    assert heuristics.detect_type("Le présent décret fixe les modalités...") == "décret"


def test_detect_type_returns_none_when_no_keyword_matches():
    assert heuristics.detect_type("Un texte sans indication de nature juridique.") is None


def test_detect_domaine_matches_known_keyword():
    assert heuristics.detect_domaine("Procédure d'exportation du cacao") == "export"


def test_detect_date_parses_textual_french_date():
    assert heuristics.detect_date("Publiée le 15 janvier 2026") == date(2026, 1, 15)


def test_detect_date_parses_numeric_date():
    assert heuristics.detect_date("Publiée le 03/02/2026") == date(2026, 2, 3)


def test_detect_date_returns_none_when_absent():
    assert heuristics.detect_date("Aucune date mentionnée ici.") is None


def test_detect_reference_matches_abbreviated_label():
    assert heuristics.detect_reference("Réf. 2026-001") == "2026-001"


def test_detect_reference_matches_full_word_label_without_capturing_the_word_itself():
    # Bug constaté par exécution réelle : "Référence : X" faisait capturer "érence" avant
    # correction (le préfixe "réf" de "référence" matchait seul, happant la suite du mot dans le
    # groupe capturé faute de garde contre les caractères suivants).
    assert heuristics.detect_reference("Référence : EXP-2026-014") == "EXP-2026-014"


def test_detect_reference_matches_degree_symbol_label():
    assert heuristics.detect_reference("Circulaire n°2026-001 relative à...") == "2026-001"


def test_detect_reference_returns_none_when_absent():
    assert heuristics.detect_reference("Aucune référence mentionnée ici.") is None
