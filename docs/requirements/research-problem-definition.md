# GoTogether Research and Problem Description

## Topic Description (Topic 3)

See also the defined [personas](./personas.md) and [user stories](./user-stories-mvp.md).

App against loneliness

- Approx. 10–14% of people in Germany feel
  lonely. Whether social networks improve or
  worsen the situation cannot be stated
  conclusively.
- Design a new social network or app that reduces
  loneliness among adolescents, adults, and
  senior citizens.
- Possible apps: social networks, dating app,
  app for shared activities, gaming app ...

## Similar Apps

1. Grouya: A matching app for spontaneous leisure activities aimed at countering social isolation. Includes a map feature and live events, but NO reward system.
   Few but very positive reviews; UX is excellent.
2. Meet5: Finding like-minded people with shared interests for leisure activities. Target audience: 40+.
3. Bumble BFF relies on a "gamified experience" through its swipe mechanic, making browsing intuitive and low-risk — but no real point system. → The swipe mechanic could be interesting for discovering new activities.

## State of Research

### 1. Passive vs. Active Usage Behavior

The way social media is used — not mere usage time — is the decisive factor in whether digital platforms amplify or alleviate loneliness. Researchers at the Joint Research Centre (JRC) of the European Commission, drawing on an EU-wide survey (2022), were the first to establish a clear pattern: passive use, defined as silently consuming content without personal interaction, is directly associated with heightened feelings of loneliness ([JRC, 2024](https://www.basicthinking.de/blog/2024/12/19/einsamkeit-und-social-media-wissenschaftler-entschluesseln-zusammenhang/); [iTopnews, 2025](https://www.itopnews.de/2025/01/neue-studie-untersucht-gefuehl-von-einsamkeit-im-zusammenhang-mit-social-media/)). By contrast, active forms of engagement — such as creating posts or communicating directly with others — can foster positive social bonds. This pattern is corroborated by a recent qualitative study describing the so-called "authenticity-visibility paradox": the more visible users become online, the less authentically they present themselves, further undermining genuine connection ([Beyond virtual proximity, Tandfonline, 2025](https://www.tandfonline.com/doi/full/10.1080/17459435.2025.2539115)). For the development of digital interventions against loneliness, a key implication follows: an app that actively motivates users to participate and engage authentically directly addresses the very mechanism that leads to isolation on purely passive platforms.

### 2. Prevalence of Loneliness Among Adolescents and Young Adults

Loneliness among young people is not a fringe phenomenon but a widespread societal problem of measurable scale. According to the German [JIM Study 2024](https://www.mpfs.de/studien/jim-studie/2024/), the average daily screen time among adolescents is 224 minutes — a level that has remained persistently high since the pandemic ([Edit Magazin, 2025](https://www.edit-magazin.de/scrollen-wir-uns-einsam.html)). A survey by IFT-Nord found that 31% of the students surveyed regularly feel lonely; among young adults aged 16 to 30, this figure rises to nearly 46% for moderate emotional loneliness ([IFT-Nord, cited in Edit Magazin, 2025](https://www.edit-magazin.de/scrollen-wir-uns-einsam.html)). A complementary US study of 1,512 adults shows that both frequency of use and time spent on social media are independently and linearly associated with loneliness ([Gorman et al., 2025, NCBI](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC12562821/)). Particularly revealing is the representative study "Generation Lonely?" by the Vodafone Foundation (Infratest dimap, 2025, n = 1,046): nearly half of the 14- to 20-year-olds surveyed use social media with the explicit goal of feeling less lonely — with those experiencing pronounced loneliness turning to social media more frequently than average ([Vodafone Stiftung, 2025](https://www.vodafone-stiftung.de/generation-einsam/)). This points to a self-reinforcing effect: those who are lonely use social media more, yet these platforms fail to sustainably alleviate feelings of loneliness.

### 3. Online vs. Offline Interaction — What Actually Helps

Despite the increasing shift of social contact into digital spaces, research consistently shows that in-person encounters are substantially superior in their impact on well-being. Multiple studies demonstrate that people report higher positive emotions, lower negative emotions, and significantly less loneliness following face-to-face interactions compared to digital ones ([Elmer et al., 2025](https://journals.sagepub.com/doi/10.1177/00936502251341088)). On a quantitative level, an Australian study shows that in-person meetings and phone calls can reduce the likelihood of loneliness among adults by 16–30% ([Social Technology Use and Loneliness, Tandfonline, 2025](https://www.tandfonline.com/doi/full/10.1080/10447318.2025.2543994)). This aligns with the so-called stimulation hypothesis: internet and app use reduces loneliness when it strengthens existing relationships or initiates new social connections — but has the opposite effect when used as a retreat from the real world ([AMA Journal of Ethics, 2023](https://journalofethics.ama-assn.org/article/internet-and-loneliness/2023-11)). A systematic review and meta-analysis of digital interventions further confirms that group-based digital approaches are considerably more effective (effect size d = −0.34) than individual ones (d = −0.16) — though both fall short of non-digital interventions (d ≈ −0.50) ([Digital bridges to social connection, ScienceDirect, 2025](https://www.sciencedirect.com/science/article/pii/S2214782925000570)). For the design of an application aimed at reducing loneliness, the conclusion is clear: the most promising approach lies not in creating additional virtual social spaces, but in building a digital platform that serves as a deliberate bridge to real, physical encounters.

## Problem Description

**TL;DR: Superficial online interactions do not create genuine relationships and amplify loneliness.**

### Sub-Problems

Superficial interaction instead of genuine relationships:

- Likes, followers, chats --> 'social activity' without emotional depth. (Lena, 17)
- Studies confirm that social media does not replace genuine connection. ([Loneliness Report TK 2024](https://www.tk.de/resource/blob/2186830/73239d0d1b389491c47f1bf7960ed254/2024-tk-einsamkeitsreport-data.pdf))

Passive social media consumption leads to loneliness:

- Merely scrolling through content exacerbates loneliness. ([JRC Study 2024](https://joint-research-centre.ec.europa.eu/jrc-news-and-updates/how-you-scroll-matters-passive-social-media-use-linked-loneliness-2024-12-13_en))
- Many feel internally isolated despite large online presence (Tom, 29).

Lack of local, real, low-barrier social contacts:

- Existing platforms are poorly suited for adults 40 and older. (Karin, 54)
- Moving increases loneliness significantly. ([Loneliness Report TK 2024](https://www.tk.de/resource/blob/2186830/73239d0d1b389491c47f1bf7960ed254/2024-tk-einsamkeitsreport-data.pdf))

### Problem Definition

Despite increasing digital connectivity through social media platforms, loneliness and lack of connection are prevalent. Superficial interactions, passive consumption, lack of local offerings, and insufficient support during life changes contribute to people feeling lonely despite online activity. There is a lack of solutions for deep social relationships and personal encounters that go beyond digital profiles.

### Project Characteristic

> A project is a goal-oriented, time-limited endeavor aimed at creating a novel product or service. (cf. lecture notes)

The following characteristics confirm the classification as a project:

- **Clarity of objectives:** The SMART goal defined below demonstrates that the project has a clear goal of combating social isolation.
- **Uniqueness and novelty:** The task is not a routine activity and brings new perspectives and approaches.
- **Complexity of the task:** The non-trivial nature of app development requires strong expertise in the field.
- **Process character with multiple work steps:** The underlying Scrum methodology organizes tasks into sprints and requires iterative refinement of the MVP.
- **Fixed deadline:** The project must be completed by the examination period within the summer semester of 2026.
- **Team formation:** A Scrum team of 5 people is involved.
- **Resource constraints:** Time and financial resources are significantly limited by the academic environment.

The endeavor can therefore be understood as a project. In terms of project type, it qualifies as a development project, characterized by a high degree of novelty and concrete objectives. The level of uncertainty is comparatively low relative to research projects.
