import './main.css';

const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

// ─── Menu mobile ────────────────────────────────────────────────────────────
const menuToggle = document.getElementById('menu-toggle');
const mobileMenu = document.getElementById('mobile-menu');
const burgerOpen  = document.getElementById('burger-open');
const burgerClose = document.getElementById('burger-close');

if (menuToggle && mobileMenu) {
    menuToggle.addEventListener('click', () => {
        mobileMenu.classList.toggle('hidden');
        const isOpen = !mobileMenu.classList.contains('hidden');
        menuToggle.setAttribute('aria-expanded', String(isOpen));
        burgerOpen.classList.toggle('hidden', isOpen);
        burgerClose.classList.toggle('hidden', !isOpen);
    });
}

// ─── Ombre header au scroll ──────────────────────────────────────────────────
const header = document.getElementById('site-header');
if (header) {
    const onScroll = () => header.classList.toggle('scrolled', window.scrollY > 8);
    window.addEventListener('scroll', onScroll, { passive: true });
    onScroll(); // appliquer l'état initial si la page est déjà scrollée
}

// ─── Animation barres de progression (section À Propos) ─────────────────────
const progressBars = document.querySelectorAll('[data-target-width]');
if (progressBars.length > 0) {
    const aboutSection = document.getElementById('about');
    const triggerEl    = aboutSection ?? document.body;

    const progressObserver = new IntersectionObserver(
        ([entry]) => {
            if (entry.isIntersecting) {
                progressBars.forEach((bar, i) => {
                    setTimeout(() => {
                        bar.style.width = bar.dataset.targetWidth + '%';
                    }, i * 180);
                });
                progressObserver.disconnect();
            }
        },
        { threshold: 0.3 }
    );
    progressObserver.observe(triggerEl);
}

// ─── Formulaire de contact (AJAX WordPress) ──────────────────────────────────
const contactForm   = document.getElementById('klem-contact-form');
const formFeedback  = document.getElementById('klem-form-feedback');
const submitBtn     = document.getElementById('klem-submit');
const submitLabel   = document.getElementById('klem-submit-label');
const submitSpinner = document.getElementById('klem-submit-spinner');

if (contactForm && window.klemAjax) {
    contactForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        submitBtn.disabled = true;
        submitLabel.textContent = 'Envoi en cours…';
        submitSpinner.classList.remove('hidden');
        formFeedback.classList.add('hidden');

        const body = new FormData(contactForm);
        body.append('action', 'klem_contact');
        body.append('klem_nonce', window.klemAjax.nonce);

        try {
            const res  = await fetch(window.klemAjax.url, { method: 'POST', body });
            const json = await res.json();

            formFeedback.classList.remove('hidden', 'bg-green-50', 'text-green-700', 'border', 'border-green-200', 'bg-red-50', 'text-red-700', 'border-red-200');

            if (json.success) {
                formFeedback.classList.add('bg-green-50', 'text-green-700', 'border', 'border-green-200');
                formFeedback.textContent = json.data.message;
                contactForm.reset();
            } else {
                formFeedback.classList.add('bg-red-50', 'text-red-700', 'border', 'border-red-200');
                formFeedback.textContent = json.data.message;
            }
        } catch {
            formFeedback.classList.remove('hidden');
            formFeedback.classList.add('bg-red-50', 'text-red-700', 'border', 'border-red-200');
            formFeedback.textContent = 'Une erreur réseau est survenue. Veuillez réessayer.';
        } finally {
            submitBtn.disabled = false;
            submitLabel.textContent = 'Envoyer le message';
            submitSpinner.classList.add('hidden');
        }
    });
}

// ─── Cartes "Secteurs ciblés" → pré-remplissage du formulaire de contact ────
const sectorLinks  = document.querySelectorAll('[data-sector]');
const messageField = document.getElementById('klem-message');

if (sectorLinks.length > 0 && messageField) {
    sectorLinks.forEach((link) => {
        link.addEventListener('click', () => {
            const sector  = link.dataset.sector;
            const isUntouched = messageField.value.trim() === '' || messageField.dataset.klemPrefilled === 'true';

            if (isUntouched) {
                messageField.value = sector === 'Autre secteur'
                    ? "Bonjour, je vous contacte au sujet d'un projet dans un secteur qui n'est pas listé sur votre site. "
                    : `Bonjour, je vous contacte au sujet d'un projet dans le secteur ${sector}. `;
                messageField.dataset.klemPrefilled = 'true';
            }

            window.setTimeout(() => messageField.focus({ preventScroll: true }), prefersReducedMotion ? 0 : 600);
        });
    });
}

// ─── Compteurs animés (statistiques du hero) ─────────────────────────────────
const counters = document.querySelectorAll('[data-count-target]');

if (counters.length > 0) {
    if (prefersReducedMotion) {
        counters.forEach((el) => {
            el.textContent = el.dataset.countTarget + (el.dataset.countSuffix ?? '');
        });
    } else {
        const countObserver = new IntersectionObserver(
            (entries) => {
                entries.forEach((entry) => {
                    if (!entry.isIntersecting) return;

                    const el       = entry.target;
                    const target   = parseFloat(el.dataset.countTarget);
                    const suffix   = el.dataset.countSuffix ?? '';
                    const decimals = parseInt(el.dataset.countDecimals ?? '0', 10);
                    const duration = 1400;
                    const start    = performance.now();

                    const step = (now) => {
                        const progress = Math.min((now - start) / duration, 1);
                        const eased    = 1 - Math.pow(1 - progress, 3);
                        el.textContent = (target * eased).toFixed(decimals) + suffix;
                        if (progress < 1) requestAnimationFrame(step);
                    };
                    requestAnimationFrame(step);
                    countObserver.unobserve(el);
                });
            },
            { threshold: 0.4 }
        );

        counters.forEach((el) => countObserver.observe(el));
    }
}

// ─── Animations d'entrée au scroll (IntersectionObserver) ───────────────────
if (prefersReducedMotion) {
    document.querySelectorAll('[data-animate]').forEach((el) => {
        el.classList.add('is-visible');
    });
} else {
    const observer = new IntersectionObserver(
        (entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    const delay = parseInt(entry.target.dataset.delay ?? '0', 10);
                    setTimeout(() => {
                        entry.target.classList.add('is-visible');
                    }, delay);
                    observer.unobserve(entry.target);
                }
            });
        },
        { threshold: 0.12, rootMargin: '0px 0px -40px 0px' }
    );

    document.querySelectorAll('[data-animate]').forEach((el) => observer.observe(el));
}
