import { useState } from 'react'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import {
  Box, Typography, TextField, Button, Stack, Alert, CircularProgress,
  Stepper, Step, StepLabel, FormControlLabel, Checkbox,
} from '@mui/material'
import CheckCircleIcon from '@mui/icons-material/CheckCircle'
import HowToRegIcon    from '@mui/icons-material/HowToReg'
import PublicSplitLayout from '../../layouts/PublicSplitLayout'
import { demandeAccesService } from '../../services/demandeAccesService'

// Numérotation ivoirienne post-2021 : 10 chiffres commençant par 0, indicatif +225/00225
// optionnel, séparateurs (espace, point, tiret) libres entre les chiffres.
const TELEPHONE_REGEX = /^(\+225|00225)?[\s.-]*0(?:[\s.-]*\d){9}$/
const MSG_TELEPHONE_INVALIDE = 'Format de téléphone invalide (10 chiffres, ex. 07 08 09 10 11)'

const schema = z
  .object({
    nom: z.string().min(1, 'Le nom est obligatoire'),
    prenom: z.string().min(1, 'Le prénom est obligatoire'),
    fonction: z.string().optional(),
    telephonePrincipal: z.string().regex(TELEPHONE_REGEX, MSG_TELEPHONE_INVALIDE),
    memeWhatsapp: z.boolean(),
    telephoneWhatsapp: z.string().optional(),
    telephoneSecondaire: z.union([z.string().regex(TELEPHONE_REGEX, MSG_TELEPHONE_INVALIDE), z.literal('')]).optional(),
    email: z.union([z.string().email("Format d'email invalide"), z.literal('')]).optional(),
    ville: z.string().min(1, 'La ville est obligatoire'),
    commune: z.string().min(1, 'La commune est obligatoire'),
    quartier: z.string().optional(),
  })
  .refine(
    (data) => data.memeWhatsapp || TELEPHONE_REGEX.test(data.telephoneWhatsapp ?? ''),
    { message: 'Renseignez le numéro WhatsApp ou cochez la case ci-dessus', path: ['telephoneWhatsapp'] },
  )

const STEPS = [
  { label: 'Identité',   fields: ['nom', 'prenom', 'fonction'] },
  { label: 'Contact',    fields: ['telephonePrincipal', 'memeWhatsapp', 'telephoneWhatsapp', 'telephoneSecondaire', 'email'] },
  { label: 'Résidence',  fields: ['ville', 'commune', 'quartier'] },
]

export default function DemandeAccesPage() {
  const [step, setStep]         = useState(0)
  const [loading, setLoading]   = useState(false)
  const [error, setError]       = useState(null)
  const [envoyee, setEnvoyee]   = useState(false)

  const { control, handleSubmit, trigger, watch, formState: { errors } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      nom: '', prenom: '', fonction: '',
      telephonePrincipal: '', memeWhatsapp: true, telephoneWhatsapp: '', telephoneSecondaire: '', email: '',
      ville: '', commune: '', quartier: '',
    },
  })

  const memeWhatsapp = watch('memeWhatsapp')

  const suivant = async () => {
    const ok = await trigger(STEPS[step].fields)
    if (ok) setStep((s) => Math.min(s + 1, STEPS.length - 1))
  }
  const precedent = () => setStep((s) => Math.max(s - 1, 0))

  const onSubmit = async (data) => {
    setLoading(true); setError(null)
    try {
      await demandeAccesService.soumettre({
        nom: data.nom,
        prenom: data.prenom,
        fonction: data.fonction || null,
        telephonePrincipal: data.telephonePrincipal,
        telephoneWhatsapp: data.memeWhatsapp ? null : data.telephoneWhatsapp,
        telephoneSecondaire: data.telephoneSecondaire || null,
        email: data.email || null,
        ville: data.ville,
        commune: data.commune,
        quartier: data.quartier || null,
      })
      setEnvoyee(true)
    } catch (err) {
      setError(err.message || "Échec de l'envoi de la demande")
    } finally {
      setLoading(false)
    }
  }

  return (
    <PublicSplitLayout
      activePage="acces"
      cardMaxWidth={600}
      heroSlot={
        <Box sx={{ py: { xs: 1, md: 3 } }}>
          <Typography variant="h3" sx={{ fontSize: { xs: '1.9rem', md: '2.3rem' }, mb: 2 }}>
            Rejoignez Cantine Connect
          </Typography>
          <Typography variant="h6" sx={{ fontWeight: 400, color: 'text.secondary', maxWidth: 460, mb: 3 }}>
            Quelques informations suffisent pour lancer votre demande. Une fois validée par
            l'établissement, vous recevrez vos identifiants de connexion.
          </Typography>
          <Stack spacing={1.5}>
            {['Vérification par l’établissement avant activation', 'Identifiants envoyés dès validation', 'Ajout de vos enfants ensuite depuis votre espace'].map((t) => (
              <Stack key={t} direction="row" spacing={1.25} alignItems="flex-start">
                <CheckCircleIcon color="secondary" fontSize="small" sx={{ mt: 0.3 }} />
                <Typography variant="body2" color="text.secondary">{t}</Typography>
              </Stack>
            ))}
          </Stack>
        </Box>
      }
    >
      <Box sx={{ p: { xs: 3, sm: 4 } }}>
        {envoyee ? (
          <Stack alignItems="center" textAlign="center" spacing={2} py={3}>
            <CheckCircleIcon color="secondary" sx={{ fontSize: 56 }} />
            <Typography variant="h6" fontWeight={700}>Demande envoyée !</Typography>
            <Typography variant="body2" color="text.secondary">
              Votre demande d'accès a bien été transmise. L'établissement va l'examiner et
              vous contactera avec vos identifiants de connexion dès validation.
            </Typography>
            <Button href="/" variant="outlined" sx={{ mt: 1, borderRadius: 999 }}>
              Retour à l'accueil
            </Button>
          </Stack>
        ) : (
          <>
            <Stack direction="row" alignItems="center" spacing={1} mb={3}>
              <HowToRegIcon fontSize="small" color="primary" />
              <Typography variant="subtitle1" fontWeight={700}>Demande d'accès parent</Typography>
            </Stack>

            <Stepper activeStep={step} alternativeLabel sx={{ mb: 3.5 }}>
              {STEPS.map((s) => <Step key={s.label}><StepLabel>{s.label}</StepLabel></Step>)}
            </Stepper>

            {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>{error}</Alert>}

            <form onSubmit={handleSubmit(onSubmit)}>
              {step === 0 && (
                <Stack spacing={2.5}>
                  <TextFieldCtl control={control} name="nom" label="Nom" errors={errors} autoFocus />
                  <TextFieldCtl control={control} name="prenom" label="Prénom" errors={errors} />
                  <TextFieldCtl control={control} name="fonction" label="Fonction (optionnel)" errors={errors} />
                </Stack>
              )}

              {step === 1 && (
                <Stack spacing={2.5}>
                  <TextFieldCtl control={control} name="telephonePrincipal" label="Téléphone principal" errors={errors} autoFocus />
                  <Alert severity="info" sx={{ mt: -1.5 }}>
                    Vous recevrez une notification par SMS dès que votre demande sera traitée.
                    Veuillez vous assurer que ce numéro est correct.
                  </Alert>
                  <Controller
                    name="memeWhatsapp"
                    control={control}
                    render={({ field }) => (
                      <FormControlLabel
                        control={<Checkbox checked={field.value} onChange={(e) => field.onChange(e.target.checked)} />}
                        label="Ce numéro est aussi mon WhatsApp"
                      />
                    )}
                  />
                  {!memeWhatsapp && (
                    <TextFieldCtl control={control} name="telephoneWhatsapp" label="Numéro WhatsApp" errors={errors} />
                  )}
                  <TextFieldCtl control={control} name="telephoneSecondaire" label="Second téléphone (optionnel)" errors={errors} />
                  <TextFieldCtl control={control} name="email" label="Email (optionnel)" errors={errors} type="email" />
                </Stack>
              )}

              {step === 2 && (
                <Stack spacing={2.5}>
                  <TextFieldCtl control={control} name="ville" label="Ville" errors={errors} autoFocus />
                  <TextFieldCtl control={control} name="commune" label="Commune" errors={errors} />
                  <TextFieldCtl control={control} name="quartier" label="Quartier (optionnel)" errors={errors} />
                </Stack>
              )}

              <Stack direction="row" justifyContent="space-between" mt={4}>
                <Button onClick={precedent} disabled={step === 0} sx={{ visibility: step === 0 ? 'hidden' : 'visible' }}>
                  Précédent
                </Button>
                {step < STEPS.length - 1 ? (
                  <Button variant="contained" onClick={suivant} sx={{ px: 4, borderRadius: 999 }}>
                    Suivant
                  </Button>
                ) : (
                  <Button type="submit" variant="contained" disabled={loading} sx={{ px: 4, borderRadius: 999 }}>
                    {loading ? <CircularProgress size={22} color="inherit" /> : 'Envoyer ma demande'}
                  </Button>
                )}
              </Stack>
            </form>
          </>
        )}
      </Box>
    </PublicSplitLayout>
  )
}

function TextFieldCtl({ control, name, label, errors, ...rest }) {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field }) => (
        <TextField
          {...field}
          label={label}
          fullWidth
          error={Boolean(errors[name])}
          helperText={errors[name]?.message}
          {...rest}
        />
      )}
    />
  )
}
