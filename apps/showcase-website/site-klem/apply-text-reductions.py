#!/usr/bin/env python3
"""
Applique les réécritures de text-reductions.json directement sur les fichiers
PHP du thème klem-theme (source de vérité du site, pas le HTML rendu en ligne
— un patch DOM sur klemtech.net ne survivrait pas au prochain rendu WordPress).

Usage :
  python3 apply-text-reductions.py                # dry-run : affiche le diff sans écrire
  python3 apply-text-reductions.py --apply         # écrit les changements sur disque
  python3 apply-text-reductions.py --apply --build # + relance `pnpm build` du thème
"""

import argparse
import json
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent
JSON_PATH = ROOT / "text-reductions.json"
THEME_DIR = ROOT / "web/app/themes/klem-theme"


def load_modifications():
    data = json.loads(JSON_PATH.read_text(encoding="utf-8"))
    for page in data.get("pages", []):
        for mod in page.get("modifications", []):
            yield mod


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true", help="Écrit les changements sur disque (sinon dry-run)")
    parser.add_argument("--build", action="store_true", help="Relance `pnpm build` après application")
    args = parser.parse_args()

    applied, skipped = [], []

    for mod in load_modifications():
        rel_path = mod["fichier"].replace("web/app/themes/klem-theme/", "")
        file_path = THEME_DIR / rel_path
        old = mod["texte_actuel"]
        new = mod["texte_propose"]
        label = mod.get("section", rel_path)

        if not file_path.exists():
            skipped.append((label, f"fichier introuvable : {file_path}"))
            continue

        content = file_path.read_text(encoding="utf-8")

        # Les chaînes PHP à guillemets simples échappent l'apostrophe (\'),
        # contrairement aux chaînes à guillemets doubles utilisées ailleurs.
        old_escaped = old.replace("'", "\\'")
        new_escaped = new.replace("'", "\\'")
        if old in content:
            search, replace = old, new
        elif old_escaped in content:
            search, replace = old_escaped, new_escaped
        else:
            skipped.append((label, "texte_actuel introuvable dans le fichier (déjà modifié ?)"))
            continue

        count = content.count(search)
        if count > 1:
            skipped.append((label, f"texte_actuel trouvé {count} fois — remplacement ambigu, ignoré"))
            continue

        print(f"--- {label} ({rel_path})")
        print(f"-  {old}")
        print(f"+  {new}\n")

        if args.apply:
            file_path.write_text(content.replace(search, replace), encoding="utf-8")

        applied.append(label)

    print(f"\n{len(applied)} bloc(s) {'modifié(s)' if args.apply else 'à modifier (dry-run)'}, {len(skipped)} ignoré(s).")
    if skipped:
        print("\nIgnorés :")
        for label, reason in skipped:
            print(f"  - {label}: {reason}")

    if args.apply and args.build:
        print("\n> pnpm build")
        subprocess.run(["pnpm", "build"], cwd=THEME_DIR, check=False)

    if args.apply:
        diff = subprocess.run(["git", "diff", "--", str(THEME_DIR)], cwd=ROOT, capture_output=True, text=True)
        (ROOT / "diff.txt").write_text(diff.stdout, encoding="utf-8")
        print(f"\nDiff écrit dans {ROOT / 'diff.txt'}")

    if not args.apply:
        print("\n(dry-run — relancer avec --apply pour écrire les fichiers)")


if __name__ == "__main__":
    sys.exit(main())
