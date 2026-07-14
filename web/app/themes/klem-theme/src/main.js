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

// ─── Chatbot (capture de leads via API Anthropic) ────────────────────────────
const chatToggle    = document.getElementById('klem-chat-toggle');
const chatClose     = document.getElementById('klem-chat-close');
const chatPanel     = document.getElementById('klem-chat-panel');
const chatIconOpen  = document.getElementById('klem-chat-icon-open');
const chatIconClose = document.getElementById('klem-chat-icon-close');
const chatMessages  = document.getElementById('klem-chat-messages');
const chatForm      = document.getElementById('klem-chat-form');
const chatInput     = document.getElementById('klem-chat-input');
const chatSend      = document.getElementById('klem-chat-send');
const chatSendIcon  = document.getElementById('klem-chat-send-icon');
const chatSendSpinner = document.getElementById('klem-chat-send-spinner');

if (chatToggle && chatPanel && chatForm && window.klemChatbotAjax) {
    const history = [];
    let isChatOpen = false;
    let isSending  = false;

    const setChatOpen = (open) => {
        isChatOpen = open;
        chatPanel.classList.toggle('hidden', !open);
        chatIconOpen.classList.toggle('hidden', open);
        chatIconClose.classList.toggle('hidden', !open);
        chatToggle.setAttribute('aria-expanded', String(open));
        if (open) {
            window.setTimeout(() => chatInput.focus(), prefersReducedMotion ? 0 : 150);
        }
    };

    chatToggle.addEventListener('click', () => setChatOpen(!isChatOpen));
    chatClose?.addEventListener('click', () => setChatOpen(false));

    const scrollChatToBottom = () => {
        chatMessages.scrollTop = chatMessages.scrollHeight;
    };

    const appendMessage = (role, text) => {
        const row = document.createElement('div');
        row.className = role === 'user' ? 'flex justify-end' : 'flex justify-start';

        const bubble = document.createElement('p');
        bubble.className = role === 'user'
            ? 'max-w-[85%] bg-klem-orange text-white text-sm leading-relaxed rounded-2xl rounded-br-sm px-4 py-2.5'
            : 'max-w-[85%] bg-white border border-gray-100 text-gray-700 text-sm leading-relaxed rounded-2xl rounded-bl-sm px-4 py-2.5 shadow-sm';
        bubble.textContent = text;

        row.appendChild(bubble);
        chatMessages.appendChild(row);
        scrollChatToBottom();
    };

    const setSending = (sending) => {
        isSending = sending;
        chatInput.disabled  = sending;
        chatSend.disabled   = sending;
        chatSendIcon.classList.toggle('hidden', sending);
        chatSendSpinner.classList.toggle('hidden', !sending);
    };

    chatForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const text = chatInput.value.trim();
        if (!text || isSending) return;

        appendMessage('user', text);
        history.push({ role: 'user', content: text });
        chatInput.value = '';
        setSending(true);

        try {
            const body = new URLSearchParams();
            body.append('action', 'klem_chatbot_message');
            body.append('nonce', window.klemChatbotAjax.nonce);
            body.append('messages', JSON.stringify(history));

            const res  = await fetch(window.klemChatbotAjax.url, { method: 'POST', body });
            const json = await res.json();

            if (json.success && json.data?.reply) {
                appendMessage('assistant', json.data.reply);
                history.push({ role: 'assistant', content: json.data.reply });
            } else {
                appendMessage('assistant', json.data?.message ?? 'Une erreur est survenue. Merci de réessayer.');
            }
        } catch {
            appendMessage('assistant', 'Une erreur réseau est survenue. Merci de réessayer.');
        } finally {
            setSending(false);
            chatInput.focus();
        }
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
