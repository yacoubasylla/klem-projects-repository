import ExcelJS from 'exceljs'

const STATUT_LABELS       = { EN_ATTENTE: 'En attente', ACCEPTE: 'Accepté', REFUSE: 'Refusé', ANNULE: 'Annulé' }
const OPERATEUR_LABELS    = { ORANGE_MONEY: 'Orange Money', MTN_MONEY: 'MTN Money', MOOV_MONEY: 'Moov Money', WAVE: 'Wave' }
const RESULTAT_LABELS     = { ACCORDE: 'Accordé', REFUSE: 'Refusé' }
const STATUT_ACCES_LABELS = { EN_ATTENTE_PAIEMENT: 'En attente de paiement', AUTORISE: 'Autorisé', GRACE: 'Période de grâce', SUSPENDU: 'Suspendu' }
const PERIODE_LABELS      = { TRIMESTRIEL: 'Trimestriel', ANNUEL: 'Annuel', NON_DEFINI: 'Non défini' }
const STATUT_DEMANDE_LABELS = { EN_ATTENTE: 'En attente', VALIDEE: 'Validée', REJETEE: 'Rejetée' }

function styleHeaderRow(ws) {
  const row = ws.getRow(1)
  row.font = { bold: true }
  row.eachCell((cell) => { cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFF5F0E6' } } })
}

export async function exporterRapportExcel({ periode, resume, paiements, passages, eleves, acces, demandes, demandesResume, inclureDemandes }) {
  const wb = new ExcelJS.Workbook()
  wb.creator = 'Cantine Connect'
  wb.created = new Date()

  const wsResume = wb.addWorksheet('Résumé')
  wsResume.columns = [
    { header: 'Indicateur', key: 'k', width: 40 },
    { header: 'Valeur', key: 'v', width: 20 },
  ]
  wsResume.addRows([
    { k: 'Période', v: `${periode.dateDebut} au ${periode.dateFin}` },
    { k: 'Montant total encaissé (XOF)', v: resume.montantEncaisse },
    { k: 'Nombre de paiements', v: resume.nbPaiements },
    { k: 'Paiements acceptés', v: resume.nbParStatut.ACCEPTE ?? 0 },
    { k: 'Paiements en attente', v: resume.nbParStatut.EN_ATTENTE ?? 0 },
    { k: 'Paiements refusés', v: resume.nbParStatut.REFUSE ?? 0 },
    { k: 'Paiements annulés', v: resume.nbParStatut.ANNULE ?? 0 },
    { k: 'Passages enregistrés', v: resume.nbPassages },
    { k: 'Passages accordés', v: resume.nbPassagesAccordes },
    { k: 'Passages refusés', v: resume.nbPassagesRefuses },
    { k: "Taux d'accès (%)", v: resume.tauxAcces ?? '—' },
  ])
  styleHeaderRow(wsResume)

  const wsPaiements = wb.addWorksheet('Paiements')
  wsPaiements.columns = [
    { header: 'Date', key: 'date', width: 18 },
    { header: 'Élève', key: 'eleve', width: 26 },
    { header: 'Opérateur', key: 'operateur', width: 16 },
    { header: 'Montant (XOF)', key: 'montant', width: 14 },
    { header: 'Téléphone', key: 'telephone', width: 16 },
    { header: 'Statut', key: 'statut', width: 14 },
    { header: 'Référence', key: 'reference', width: 20 },
  ]
  paiements.forEach((p) => wsPaiements.addRow({
    date:      p.dateCreation ? new Date(p.dateCreation).toLocaleString('fr-FR') : '',
    eleve:     p.eleveNomComplet ?? '',
    operateur: OPERATEUR_LABELS[p.operateur] ?? p.operateur,
    montant:   Number(p.montant ?? 0),
    telephone: p.telephonePayeur ?? '',
    statut:    STATUT_LABELS[p.statut] ?? p.statut,
    reference: p.referenceInterne ?? '',
  }))
  styleHeaderRow(wsPaiements)

  const wsPassages = wb.addWorksheet('Passages')
  wsPassages.columns = [
    { header: 'Date', key: 'date', width: 14 },
    { header: 'Heure', key: 'heure', width: 12 },
    { header: 'Matricule', key: 'matricule', width: 14 },
    { header: 'Élève', key: 'eleve', width: 26 },
    { header: 'Classe', key: 'classe', width: 16 },
    { header: 'Établissement', key: 'etablissement', width: 22 },
    { header: 'Résultat', key: 'resultat', width: 14 },
    { header: 'Motif de refus', key: 'motif', width: 22 },
  ]
  passages.forEach((p) => wsPassages.addRow({
    date:          p.datePassage ?? '',
    heure:         p.heurePassage ? new Date(p.heurePassage).toLocaleTimeString('fr-FR') : '',
    matricule:     p.eleveMatricule ?? '',
    eleve:         p.eleveNomComplet ?? '',
    classe:        p.classeNom ?? '',
    etablissement: p.etablissementNom ?? '',
    resultat:      RESULTAT_LABELS[p.resultat] ?? p.resultat,
    motif:         p.motifRefus ?? '',
  }))
  styleHeaderRow(wsPassages)

  // ── Accès (photographie actuelle, indépendante de la période) ──────────────
  const wsAcces = wb.addWorksheet('Accès')
  wsAcces.columns = [
    { header: 'Matricule', key: 'matricule', width: 14 },
    { header: 'Élève', key: 'eleve', width: 26 },
    { header: 'Établissement', key: 'etablissement', width: 22 },
    { header: 'Classe', key: 'classe', width: 16 },
    { header: "Statut d'accès", key: 'statut', width: 20 },
    { header: 'Période abonnement', key: 'periode', width: 18 },
  ]
  ;(eleves ?? []).forEach((e) => wsAcces.addRow({
    matricule:     e.matricule ?? '',
    eleve:         `${e.prenom ?? ''} ${e.nom ?? ''}`.trim(),
    etablissement: e.etablissementNom ?? '',
    classe:        e.classeLibelle ?? '',
    statut:        STATUT_ACCES_LABELS[e.statutAcces] ?? e.statutAcces,
    periode:       PERIODE_LABELS[e.periodeAbonnement ?? 'NON_DEFINI'],
  }))
  styleHeaderRow(wsAcces)
  if (acces) {
    wsAcces.addRow({})
    wsAcces.addRow({ matricule: `Total : ${acces.total}` })
  }

  // ── Demandes d'accès (ADMIN uniquement) ─────────────────────────────────────
  if (inclureDemandes) {
    const wsDemandes = wb.addWorksheet("Demandes d'accès")
    wsDemandes.columns = [
      { header: 'Soumise le', key: 'date', width: 18 },
      { header: 'Demandeur', key: 'demandeur', width: 26 },
      { header: 'Téléphone', key: 'telephone', width: 16 },
      { header: 'Statut', key: 'statut', width: 14 },
      { header: 'Traitée le', key: 'traitee', width: 18 },
      { header: 'Motif de rejet', key: 'motif', width: 26 },
    ]
    ;(demandes ?? []).forEach((d) => wsDemandes.addRow({
      date:      d.dateSoumission ? new Date(d.dateSoumission).toLocaleString('fr-FR') : '',
      demandeur: `${d.prenom ?? ''} ${d.nom ?? ''}`.trim(),
      telephone: d.telephonePrincipal ?? '',
      statut:    STATUT_DEMANDE_LABELS[d.statut] ?? d.statut,
      traitee:   d.dateTraitement ? new Date(d.dateTraitement).toLocaleString('fr-FR') : '',
      motif:     d.motifRejet ?? '',
    }))
    styleHeaderRow(wsDemandes)
    if (demandesResume) {
      wsDemandes.addRow({})
      wsDemandes.addRow({ date: `Total : ${demandesResume.total}`, demandeur: `Délai moyen : ${demandesResume.delaiMoyenHeures ?? '—'} h` })
    }
  }

  const buffer = await wb.xlsx.writeBuffer()
  const blob = new Blob([buffer], {
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `rapport_${periode.dateDebut}_${periode.dateFin}.xlsx`
  a.click()
  URL.revokeObjectURL(url)
}

export const RAPPORT_LABELS = { STATUT_LABELS, OPERATEUR_LABELS, RESULTAT_LABELS, STATUT_ACCES_LABELS, PERIODE_LABELS, STATUT_DEMANDE_LABELS }
