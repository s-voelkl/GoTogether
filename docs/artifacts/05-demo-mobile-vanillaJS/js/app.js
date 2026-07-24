/* ═══════════════════════════════════════
   GoTogether — Frontend Prototype
   State · Data · Screens · Logic
═══════════════════════════════════════ */

// ══════════════════════════════════════
// STATE
// ══════════════════════════════════════
const state = {
  battery: 72,
  coins: 1250,
  xp: 2340,
  level: 12,
  xpMax: 4000,
  joinedQuests: new Set(),
  connectedUsers: new Set(),
  redeemedRewards: new Set(),
  activeInterests: new Set(['Wandern', 'Kunst', 'Kaffee']),
  currentMatchIndex: 0,
  activeScreen: 'home',
  homeFilter: 'all',
  mapFilter: 'quests',
  matchFilter: 'all',
};

function saveState() {
  const s = { ...state };
  s.joinedQuests   = [...state.joinedQuests];
  s.connectedUsers = [...state.connectedUsers];
  s.redeemedRewards= [...state.redeemedRewards];
  s.activeInterests= [...state.activeInterests];
  localStorage.setItem('gt_state', JSON.stringify(s));
}

function loadState() {
  try {
    const raw = localStorage.getItem('gt_state');
    if (!raw) return;
    const s = JSON.parse(raw);
    state.battery       = s.battery ?? 72;
    state.coins         = s.coins  ?? 1250;
    state.xp            = s.xp    ?? 2340;
    state.level         = s.level ?? 12;
    state.currentMatchIndex = s.currentMatchIndex ?? 0;
    state.joinedQuests   = new Set(s.joinedQuests  ?? []);
    state.connectedUsers = new Set(s.connectedUsers ?? []);
    state.redeemedRewards= new Set(s.redeemedRewards?? []);
    state.activeInterests= new Set(s.activeInterests ?? ['Wandern','Kunst','Kaffee']);
  } catch(e) { /* ignore */ }
}

// ══════════════════════════════════════
// DATA
// ══════════════════════════════════════
const QUESTS = [
  { id:1, name:'Café-Hop Maxvorstadt',      emoji:'🎯', color:'purple-l', pinClass:'pd-purple', distance:'380m',  participants:4,  max:8,  time:'14:00', xp:120, coins:50,  cat:'social',   bat:'medium', topics:['Kaffee','Social'],    mapX:'40%', mapY:'30%' },
  { id:2, name:'Kunstworkshop Schwabing',   emoji:'🎨', color:'pink-l',   pinClass:'pd-pink',   distance:'0.8km', participants:6,  max:12, time:'16:30', xp:200, coins:80,  cat:'creative', bat:'medium', topics:['Kunst','Kreativ'],    mapX:'20%', mapY:'48%' },
  { id:3, name:'Park-Picknick Engl. Garten',emoji:'🌿', color:'green-l',  pinClass:'pd-green',  distance:'1.2km', participants:11, max:20, time:'13:00', xp:80,  coins:30,  cat:'outdoor',  bat:'high',   topics:['Outdoor','Natur'],    mapX:'64%', mapY:'63%' },
  { id:4, name:'Fotografie-Stadtspaziergang',emoji:'📸',color:'coral-l',  pinClass:'pd-coral',  distance:'0.5km', participants:3,  max:8,  time:'15:00', xp:150, coins:60,  cat:'creative', bat:'medium', topics:['Foto','Stadtleben'],  mapX:'72%', mapY:'24%' },
  { id:5, name:'Brettspielabend Schwabing', emoji:'🎲', color:'blue-l',   pinClass:'pd-purple', distance:'0.9km', participants:2,  max:6,  time:'19:00', xp:100, coins:40,  cat:'social',   bat:'low',    topics:['Spiele','Indoor'],    mapX:'33%', mapY:'70%' },
  { id:6, name:'Team-Raid: Stadtputzaktion',emoji:'🏆', color:'gold-l',   pinClass:'pd-gold',   distance:'2.1km', participants:18, max:30, time:'10:00', xp:300, coins:150, cat:'raid',     bat:'high',   topics:['Team','Umwelt'],      mapX:'58%', mapY:'18%' },
];

const MATCHES = [
  { id:1, name:'Lena K.',   age:24, emoji:'👩', av:'purple-l', distance:'0.5km', score:94, interests:['Wandern','Fotografie','Hunde'],  bio:'Ich liebe es, neue Orte zu entdecken! 📸 Immer auf der Suche nach echten Begegnungen.' },
  { id:2, name:'Marcel R.', age:34, emoji:'🧑', av:'coral-l',  distance:'1.1km', score:87, interests:['Musik','Events','Kochen'],        bio:'Auf der Suche nach echten Verbindungen — Social Media reicht mir nicht mehr.' },
  { id:3, name:'Karin M.',  age:54, emoji:'👩', av:'green-l',  distance:'0.8km', score:81, interests:['Kunst','Lesen','Hunde'],          bio:'Neu in München und freue mich sehr auf neue Bekanntschaften!' },
  { id:4, name:'Tom S.',    age:29, emoji:'🧑', av:'blue-l',   distance:'1.5km', score:78, interests:['Sport','Kochen','Musik'],         bio:'Social-Media-müde und auf der Suche nach echten Kontakten.' },
  { id:5, name:'Julia W.',  age:22, emoji:'👩', av:'pink-l',   distance:'0.3km', score:76, interests:['Kunst','Fotografie','Kaffee'],    bio:'Studentin, immer auf der Suche nach Inspiration und neuen Freunden!' },
  { id:6, name:'Erik B.',   age:31, emoji:'🧑', av:'gold-l',   distance:'2.0km', score:72, interests:['Wandern','Sport','Natur'],        bio:'Outdoor-Fan der echte Abenteuer liebt — digital reicht mir nicht.' },
];

const REWARDS = [
  { id:1, emoji:'☕', name:'Café Muse — 10% Rabatt',      cost:500, bg:'gold-l',   desc:'Lokaler Partner · Schwabing' },
  { id:2, emoji:'🎨', name:'Avatar-Skin „Galaxy"',         cost:300, bg:'pink-l',   desc:'Saisonale Edition · Limitiert' },
  { id:3, emoji:'🎫', name:'Stadtfest-Ticket',             cost:800, bg:'purple-l', desc:'Event · 15. Juni 2026' },
  { id:4, emoji:'⚡', name:'Doppelte XP für 24h',          cost:200, bg:'coral-l',  desc:'Boost für einen Tag' },
  { id:5, emoji:'🍕', name:'Pizzeria Bella — Gratis Dessert', cost:400, bg:'green-l', desc:'Lokaler Partner · Maxvorstadt' },
  { id:6, emoji:'🐾', name:'Virtuelles Haustier „Buddy"', cost:600, bg:'blue-l',   desc:'Dein Quest-Begleiter · AR-kompatibel' },
];

const BADGES = [
  { emoji:'🎯', name:'Erster\nCheck-in', earned:true  },
  { emoji:'🗺️', name:'5 Quests',        earned:true  },
  { emoji:'👥', name:'Team\nPlayer',    earned:true  },
  { emoji:'🏙️', name:'Stadt-\nentdecker',earned:true },
  { emoji:'⚡', name:'Early\nBird',     earned:true  },
  { emoji:'🏆', name:'Raid\nBoss',      earned:false },
  { emoji:'💎', name:'Diamond',         earned:false },
  { emoji:'🌟', name:'Superstar',       earned:false },
  { emoji:'🦋', name:'Wandlung',        earned:false },
  { emoji:'🎪', name:'Event-\nKönig',   earned:false },
];

const ALL_INTERESTS = ['Wandern','Kunst','Kaffee','Musik','Events','Hunde','Kochen','Fotografie','Lesen','Sport','Natur','Spiele'];

// ══════════════════════════════════════
// NAVIGATION
// ══════════════════════════════════════
function navigate(id) {
  document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  document.getElementById('screen-' + id)?.classList.add('active');
  document.querySelector(`.nav-item[data-screen="${id}"]`)?.classList.add('active');
  state.activeScreen = id;
  renders[id]?.();
}

const renders = {
  home:     renderHome,
  map:      renderMap,
  matching: renderMatching,
  rewards:  renderRewards,
  profile:  renderProfile,
};

// ══════════════════════════════════════
// TOAST
// ══════════════════════════════════════
function toast(msg, type = 'info', icon = '') {
  const c = document.getElementById('toast-container');
  const t = document.createElement('div');
  t.className = `toast ${type}`;
  t.innerHTML = `${icon ? `<span>${icon}</span>` : ''}<span>${msg}</span>`;
  c.appendChild(t);
  setTimeout(() => t.remove(), 2800);
}

// ══════════════════════════════════════
// MODAL / DETAIL SHEET
// ══════════════════════════════════════
function openSheet(html) {
  const overlay = document.getElementById('modal-overlay');
  document.getElementById('modal-content').innerHTML = html;
  overlay.classList.add('open');
}
function closeSheet() {
  document.getElementById('modal-overlay').classList.remove('open');
}

// ══════════════════════════════════════
// HELPER
// ══════════════════════════════════════
function batteryLabel(v) {
  if (v < 35) return { text:'Wenig Energie 😴', rec:'Ruhige 1:1-Aktivitäten', state:[true,false,false] };
  if (v < 70) return { text:'Gut aufgeladen 👥', rec:'Kleine Gruppen-Quests', state:[false,true,false] };
  return { text:'Volle Power! 🔥', rec:'Team-Raids & Events',  state:[false,false,true] };
}

function catFilter(q) {
  if (state.homeFilter === 'all') return true;
  return q.cat === state.homeFilter;
}

function batFilter(q) {
  const v = state.battery;
  if (v < 35) return q.bat === 'low';
  if (v < 70) return ['low','medium'].includes(q.bat);
  return true;
}

function questColor(q) {
  const map = {'purple-l':'var(--purple-l)','pink-l':'var(--pink-l)','green-l':'var(--green-l)','coral-l':'var(--coral-l)','blue-l':'var(--blue-l)','gold-l':'var(--gold-l)'};
  return map[q.color] || 'var(--white)';
}

function updateCoinsDisplay() {
  document.querySelectorAll('.live-coins').forEach(el => el.textContent = state.coins.toLocaleString('de-DE'));
}

// ══════════════════════════════════════
// HOME SCREEN
// ══════════════════════════════════════
function renderHome() {
  const bl = batteryLabel(state.battery);
  const filtered = QUESTS.filter(q => catFilter(q));
  const recFiltered = QUESTS.filter(q => batFilter(q) && !state.joinedQuests.has(q.id)).slice(0, 2);

  const cats = [
    { key:'all', label:'Alle' },
    { key:'social', label:'🤝 Social' },
    { key:'creative', label:'🎨 Kreativ' },
    { key:'outdoor', label:'🌿 Outdoor' },
    { key:'raid', label:'🏆 Raid' },
  ];

  document.getElementById('home-battery-val').textContent = state.battery + '%';
  document.getElementById('home-battery-text').textContent = bl.text;
  document.getElementById('home-battery-rec').textContent = '→ ' + bl.rec;
  document.getElementById('home-battery-slider').value = state.battery;
  document.getElementById('bat-1').className = 'bat-state ' + (bl.state[0] ? 'on' : 'off');
  document.getElementById('bat-2').className = 'bat-state ' + (bl.state[1] ? 'on' : 'off');
  document.getElementById('bat-3').className = 'bat-state ' + (bl.state[2] ? 'on' : 'off');

  // filter chips
  document.getElementById('home-filters').innerHTML = cats.map(c =>
    `<div class="chip ${state.homeFilter===c.key?'chip-active':'chip-default'}" onclick="setHomeFilter('${c.key}')">${c.label}</div>`
  ).join('');

  // recommended
  document.getElementById('home-rec').innerHTML = recFiltered.length
    ? recFiltered.map(q => questCardHTML(q, true)).join('')
    : `<div class="empty-state"><div class="empty-icon">⚡</div><div class="empty-text">Energie aufladen für mehr Quests!</div></div>`;

  // all quests
  document.getElementById('home-quests').innerHTML = filtered.map(q => questCardHTML(q, false)).join('');

  updateCoinsDisplay();
}

function questCardHTML(q, compact) {
  const joined = state.joinedQuests.has(q.id);
  const full = q.participants >= q.max;
  return `
    <div class="quest-card" onclick="openQuestDetail(${q.id})" style="background:${questColor(q)}">
      <div class="quest-icon" style="background:rgba(255,255,255,.7);">${q.emoji}</div>
      <div class="quest-info">
        <div class="quest-name">${q.name}</div>
        <div class="quest-meta">📍 ${q.distance} · 👥 ${q.participants}/${q.max} · ⏱ ${q.time}</div>
      </div>
      ${joined
        ? `<div class="joined-badge">✓ Dabei</div>`
        : `<div class="quest-xp" style="background:rgba(255,255,255,.6);color:var(--black);">+${q.xp} XP</div>`
      }
    </div>`;
}

function setHomeFilter(cat) {
  state.homeFilter = cat;
  renderHome();
}

function openQuestDetail(id) {
  const q = QUESTS.find(x => x.id === id);
  if (!q) return;
  const joined = state.joinedQuests.has(id);
  const full = q.participants >= q.max;
  openSheet(`
    <div class="sheet-handle"></div>
    <div style="display:flex;align-items:center;gap:12px;margin-bottom:14px;">
      <div style="width:52px;height:52px;border-radius:16px;border:var(--border-sm);background:${questColor(q)};display:flex;align-items:center;justify-content:center;font-size:26px;box-shadow:var(--sh-sm);">${q.emoji}</div>
      <div>
        <div style="font-size:16px;font-weight:900;">${q.name}</div>
        <div style="font-size:11px;opacity:.6;margin-top:2px;">📍 ${q.distance} · ⏱ ${q.time} Uhr</div>
      </div>
    </div>
    <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:8px;margin-bottom:14px;">
      <div style="background:var(--purple-l);border:var(--border-xs);border-radius:12px;padding:10px;text-align:center;box-shadow:var(--sh-xs);">
        <div style="font-size:18px;font-weight:900;color:var(--purple);">+${q.xp}</div>
        <div style="font-size:9px;font-weight:700;opacity:.6;text-transform:uppercase;">XP</div>
      </div>
      <div style="background:var(--gold-l);border:var(--border-xs);border-radius:12px;padding:10px;text-align:center;box-shadow:var(--sh-xs);">
        <div style="font-size:18px;font-weight:900;color:var(--gold);">+${q.coins}</div>
        <div style="font-size:9px;font-weight:700;opacity:.6;text-transform:uppercase;">🪙</div>
      </div>
      <div style="background:var(--green-l);border:var(--border-xs);border-radius:12px;padding:10px;text-align:center;box-shadow:var(--sh-xs);">
        <div style="font-size:18px;font-weight:900;color:var(--green);">${q.participants}/${q.max}</div>
        <div style="font-size:9px;font-weight:700;opacity:.6;text-transform:uppercase;">👥</div>
      </div>
    </div>
    <div style="display:flex;gap:6px;flex-wrap:wrap;margin-bottom:16px;">
      ${q.topics.map(t => `<div class="chip chip-default">${t}</div>`).join('')}
    </div>
    <div style="display:flex;gap:8px;">
      <button class="btn btn-ghost btn-sm" onclick="closeSheet()" style="flex:1;">← Zurück</button>
      ${joined
        ? `<div class="btn btn-green" style="flex:2;cursor:default;">✓ Du nimmst teil!</div>`
        : full
          ? `<div class="btn btn-ghost btn-disabled" style="flex:2;">Ausgebucht</div>`
          : `<button class="btn btn-primary" style="flex:2;" onclick="joinQuest(${id})">🎯 Teilnehmen</button>`
      }
    </div>
  `);
}

function joinQuest(id) {
  const q = QUESTS.find(x => x.id === id);
  if (!q || state.joinedQuests.has(id)) return;
  state.joinedQuests.add(id);
  q.participants = Math.min(q.participants + 1, q.max);
  state.xp    += q.xp;
  state.coins += q.coins;
  if (state.xp >= state.xpMax) { state.level++; state.xp -= state.xpMax; toast(`🎉 Level Up! Du bist jetzt Level ${state.level}!`, 'success', '⚡'); }
  saveState();
  closeSheet();
  toast(`✓ Du nimmst an "${q.name}" teil! +${q.xp} XP, +${q.coins} 🪙`, 'success', '🎯');
  renderHome();
  updateCoinsDisplay();
}

// ══════════════════════════════════════
// MAP SCREEN
// ══════════════════════════════════════
function renderMap() {
  const filters = [
    { key:'quests',  label:'🗺️ Quests' },
    { key:'events',  label:'🎉 Events' },
    { key:'groups',  label:'👥 Gruppen' },
    { key:'raids',   label:'🏆 Raids' },
  ];

  document.getElementById('map-filters').innerHTML = filters.map(f =>
    `<div class="chip ${state.mapFilter===f.key?'chip-active':'chip-default'}" onclick="setMapFilter('${f.key}')">${f.label}</div>`
  ).join('');

  renderMapPins();
  renderMapCards();
}

function renderMapPins() {
  const container = document.getElementById('map-pins');
  const visible = QUESTS.filter(q => state.mapFilter === 'raids' ? q.cat==='raid' : state.mapFilter==='quests' || true);
  container.innerHTML = visible.map(q => `
    <div class="map-pin" style="left:${q.mapX};top:${q.mapY};" onclick="openQuestDetail(${q.id})">
      <div class="pin-dot ${q.pinClass}"><span>${q.emoji}</span></div>
      <div class="pin-label">${q.name.split(' ').slice(0,2).join(' ')}</div>
    </div>
  `).join('');
}

function renderMapCards() {
  const shown = QUESTS.slice(0, 3);
  document.getElementById('map-cards').innerHTML = shown.map(q => `
    <div class="map-mini-card" onclick="openQuestDetail(${q.id})">
      <div class="quest-icon" style="background:${questColor(q)};border:var(--border-xs);">${q.emoji}</div>
      <div style="flex:1;min-width:0;">
        <div style="font-size:12px;font-weight:800;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${q.name}</div>
        <div style="font-size:10px;opacity:.6;margin-top:2px;">📍 ${q.distance} · 👥 ${q.participants}/${q.max}</div>
      </div>
      <div style="font-size:11px;font-weight:900;color:var(--purple);">+${q.xp} XP</div>
    </div>
  `).join('');
}

function setMapFilter(f) {
  state.mapFilter = f;
  renderMap();
}

function handleMapSearch(val) {
  const v = val.toLowerCase();
  document.querySelectorAll('.map-pin').forEach(pin => {
    const label = pin.querySelector('.pin-label').textContent.toLowerCase();
    pin.style.display = label.includes(v) ? 'flex' : 'none';
  });
}

// ══════════════════════════════════════
// MATCHING SCREEN
// ══════════════════════════════════════
function renderMatching() {
  const available = MATCHES.filter(m => !state.connectedUsers.has(m.id));
  const idx = state.currentMatchIndex;

  // update counter badge
  document.getElementById('match-count').textContent = state.connectedUsers.size;

  // filter chips
  const interestFilters = ['all', 'Wandern', 'Kunst', 'Musik', 'Sport'];
  document.getElementById('match-filters').innerHTML = interestFilters.map(f =>
    `<div class="chip ${state.matchFilter===f?'chip-active':'chip-default'}" onclick="setMatchFilter('${f}')">${f === 'all' ? 'Alle' : f}</div>`
  ).join('');

  const filtered = MATCHES.filter(m =>
    !state.connectedUsers.has(m.id) &&
    (state.matchFilter === 'all' || m.interests.includes(state.matchFilter))
  );

  const stack = document.getElementById('match-stack');

  if (filtered.length === 0) {
    stack.innerHTML = `
      <div style="height:360px;display:flex;flex-direction:column;align-items:center;justify-content:center;text-align:center;border:var(--border);border-radius:24px;background:var(--white);box-shadow:var(--sh);">
        <div style="font-size:48px;margin-bottom:12px;">✨</div>
        <div style="font-size:16px;font-weight:900;margin-bottom:6px;">Alle gesehen!</div>
        <div style="font-size:13px;opacity:.6;">Filter ändern oder morgen wiederkommen.</div>
      </div>`;
    document.getElementById('match-actions').style.display = 'none';
    return;
  }

  document.getElementById('match-actions').style.display = 'grid';

  stack.innerHTML = filtered.slice(0, 3).reverse().map((m, i, arr) => {
    const isTop = i === arr.length - 1;
    return `
      <div class="match-card ${isTop ? 'card-front' : 'card-behind'}" data-match-id="${m.id}">
        <div style="display:flex;align-items:center;gap:12px;margin-bottom:12px;">
          <div class="match-avatar-big" style="background:var(--${m.av});">${m.emoji}</div>
          <div style="flex:1;">
            <div style="font-size:18px;font-weight:900;">${m.name}, ${m.age}</div>
            <div style="font-size:11px;opacity:.6;margin-bottom:6px;">📍 ${m.distance} entfernt</div>
            <div class="match-score-badge">✨ ${m.score}% Match</div>
          </div>
        </div>
        <div style="font-size:13px;line-height:1.6;color:var(--black);opacity:.75;margin-bottom:12px;flex:1;">${m.bio}</div>
        <div style="display:flex;flex-wrap:wrap;gap:6px;">
          ${m.interests.map(int => `<div class="chip chip-default" style="font-size:11px;">${int}</div>`).join('')}
        </div>
      </div>`;
  }).join('');
}

function setMatchFilter(f) {
  state.matchFilter = f;
  renderMatching();
}

function passMatch() {
  const top = document.querySelector('.match-card.card-front');
  if (!top) return;
  top.classList.add('swipe-left');
  setTimeout(() => { renderMatching(); }, 350);
  toast('Weiter geschaut 👋', 'info', '👋');
}

function connectMatch() {
  const top = document.querySelector('.match-card.card-front');
  if (!top) return;
  const id = parseInt(top.dataset.matchId);
  const m = MATCHES.find(x => x.id === id);
  state.connectedUsers.add(id);
  top.classList.add('swipe-right');
  setTimeout(() => { renderMatching(); }, 350);
  toast(`Verbunden mit ${m?.name}! 🎉`, 'success', '✨');
  saveState();
}

// ══════════════════════════════════════
// REWARDS SCREEN
// ══════════════════════════════════════
function renderRewards() {
  document.getElementById('coins-display').textContent = state.coins.toLocaleString('de-DE');
  document.getElementById('xp-display').textContent = `${state.xp.toLocaleString('de-DE')} / ${state.xpMax.toLocaleString('de-DE')} XP`;
  document.getElementById('level-display').textContent = `Level ${state.level}`;
  document.getElementById('xp-bar').style.width = Math.min(100, (state.xp / state.xpMax) * 100) + '%';

  document.getElementById('badges-grid').innerHTML = BADGES.map(b => `
    <div class="badge-item">
      <div class="badge-icon ${b.earned ? 'earned' : 'locked'}">${b.emoji}</div>
      <div class="badge-name">${b.name}</div>
    </div>
  `).join('');

  document.getElementById('rewards-list').innerHTML = REWARDS.map(r => {
    const redeemed = state.redeemedRewards.has(r.id);
    const canAfford = state.coins >= r.cost;
    return `
      <div class="reward-item" style="background:var(--${r.bg});">
        <div class="reward-icon" style="background:rgba(255,255,255,.7);">${r.emoji}</div>
        <div class="reward-info">
          <div class="reward-name">${r.name}</div>
          <div class="reward-desc">${r.desc}</div>
        </div>
        ${redeemed
          ? `<div class="joined-badge">✓</div>`
          : `<button class="btn btn-xs ${canAfford ? 'btn-black' : 'btn-ghost btn-disabled'}"
               onclick="redeemReward(${r.id})" ${!canAfford ? 'disabled' : ''}>
               ${r.cost} 🪙
             </button>`
        }
      </div>`;
  }).join('');
}

function redeemReward(id) {
  const r = REWARDS.find(x => x.id === id);
  if (!r || state.redeemedRewards.has(id)) return;
  if (state.coins < r.cost) { toast('Nicht genug GoCoins! 🪙', 'error', '❌'); return; }
  state.coins -= r.cost;
  state.redeemedRewards.add(id);
  saveState();
  toast(`${r.emoji} "${r.name}" eingelöst!`, 'gold', '🎉');
  renderRewards();
  updateCoinsDisplay();
}

// ══════════════════════════════════════
// PROFILE SCREEN
// ══════════════════════════════════════
function renderProfile() {
  const xpPct = Math.min(100, (state.xp / state.xpMax) * 100);
  document.getElementById('profile-level').textContent = `Level ${state.level}`;
  document.getElementById('profile-xp-bar').style.width = xpPct + '%';
  document.getElementById('profile-xp-text').textContent = `${state.xp.toLocaleString('de-DE')} / ${state.xpMax.toLocaleString('de-DE')} XP`;
  document.getElementById('profile-quests').textContent = state.joinedQuests.size;
  document.getElementById('profile-friends').textContent = state.connectedUsers.size;
  document.getElementById('profile-coins').textContent = state.coins.toLocaleString('de-DE');

  document.getElementById('interest-grid').innerHTML = ALL_INTERESTS.map(i => `
    <div class="interest-chip ${state.activeInterests.has(i) ? 'selected' : ''}"
         onclick="toggleInterest('${i}')">${i}</div>
  `).join('');

  document.getElementById('profile-badges').innerHTML = BADGES.filter(b => b.earned).map(b => `
    <div class="badge-item">
      <div class="badge-icon earned">${b.emoji}</div>
      <div class="badge-name">${b.name}</div>
    </div>
  `).join('');

  const joined = QUESTS.filter(q => state.joinedQuests.has(q.id));
  document.getElementById('profile-joined-quests').innerHTML = joined.length
    ? joined.map(q => `
        <div class="quest-card" style="background:${questColor(q)}">
          <div class="quest-icon" style="background:rgba(255,255,255,.7);">${q.emoji}</div>
          <div class="quest-info">
            <div class="quest-name">${q.name}</div>
            <div class="quest-meta">📍 ${q.distance} · ⏱ ${q.time}</div>
          </div>
          <div class="joined-badge">✓</div>
        </div>`).join('')
    : `<div class="empty-state"><div class="empty-icon">🗺️</div><div class="empty-text">Noch keine Quests — los geht's!</div></div>`;
}

function toggleInterest(i) {
  if (state.activeInterests.has(i)) {
    if (state.activeInterests.size <= 1) { toast('Mindestens 1 Interesse behalten!', 'error', '⚠️'); return; }
    state.activeInterests.delete(i);
    toast(`"${i}" entfernt`, 'info');
  } else {
    state.activeInterests.add(i);
    toast(`"${i}" hinzugefügt ✓`, 'success');
  }
  saveState();
  renderProfile();
}

// ══════════════════════════════════════
// BATTERY SLIDER
// ══════════════════════════════════════
function onBatteryChange(val) {
  state.battery = parseInt(val);
  saveState();
  renderHome();
}

// ══════════════════════════════════════
// INIT
// ══════════════════════════════════════
function init() {
  loadState();
  navigate('home');

  // close sheet on overlay click
  document.getElementById('modal-overlay').addEventListener('click', function(e) {
    if (e.target === this) closeSheet();
  });
}

document.addEventListener('DOMContentLoaded', init);
