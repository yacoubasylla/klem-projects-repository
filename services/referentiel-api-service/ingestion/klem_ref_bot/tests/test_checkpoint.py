import os

from klem_ref_bot import checkpoint
from klem_ref_bot.models import ExtractedTexteReglementaire


def _proposal(type_, reference):
    return ExtractedTexteReglementaire(
        titre="Titre", type=type_, date_publication=None, reference=reference,
        domaine=None, url_source=None,
    )


def test_load_returns_empty_dict_when_file_absent(tmp_path):
    path = os.path.join(tmp_path, "does_not_exist.json")

    assert checkpoint.load(path) == {}


def test_save_then_load_round_trip(tmp_path):
    path = os.path.join(tmp_path, "state.json")

    checkpoint.save({"circulaire": 2413, "note": 115}, path)
    loaded = checkpoint.load(path)

    assert loaded == {"circulaire": 2413, "note": 115}


def test_save_creates_missing_parent_directories(tmp_path):
    path = os.path.join(tmp_path, "nested", "dir", "state.json")

    checkpoint.save({"circulaire": 1}, path)

    assert os.path.exists(path)


def test_filter_new_keeps_proposals_with_no_prior_state_for_their_type():
    proposals = [_proposal("circulaire", "2413")]

    kept = checkpoint.filter_new(proposals, state={})

    assert kept == proposals


def test_filter_new_excludes_already_seen_numero():
    proposals = [_proposal("circulaire", "2413"), _proposal("circulaire", "2412")]

    kept = checkpoint.filter_new(proposals, state={"circulaire": 2412})

    assert [p.reference for p in kept] == ["2413"]


def test_filter_new_keeps_proposal_when_reference_is_not_numeric():
    proposals = [_proposal("circulaire", "N/A")]

    kept = checkpoint.filter_new(proposals, state={"circulaire": 9999})

    assert kept == proposals


def test_update_tracks_max_numero_per_type():
    state = {"circulaire": 2410}
    proposals = [_proposal("circulaire", "2413"), _proposal("circulaire", "2412"), _proposal("note", "115")]

    new_state = checkpoint.update(state, proposals)

    assert new_state == {"circulaire": 2413, "note": 115}


def test_update_does_not_mutate_input_state():
    state = {"circulaire": 2410}

    checkpoint.update(state, [_proposal("circulaire", "2413")])

    assert state == {"circulaire": 2410}
