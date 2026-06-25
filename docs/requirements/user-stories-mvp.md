# GoTogether User Stories

## User Stories

### Content User Stories

1. **Social Battery:** As a user, I want to enter my social energy level when first opening the app and be able to adjust it flexibly throughout the day, so that I receive suggestions that match my current emotional capacity (e.g., quiet meetups vs. group quests).
2. **Local Challenges:** As someone wanting to meet new people in my area, I want to choose from a selection of local quests/challenges, so that I can easily connect with real people nearby and overcome social barriers.
3. **Interest Matching:** As a user with specific hobbies and passions, I want to enter my interests with priorities, so that I can find people with whom I share common ground for genuine conversations or activities.
4. **Team Suggestions:** As a socially open person who still seeks structure (e.g., Marcel, 34), I want to receive a team suggestion based on my profile, so that I feel part of a group and have recurring social touchpoints.
5. **AI Assistant:** As a user, I want to have an understanding, safe, and discreet AI assistant that motivates me, suggests suitable activities, and reflects my progress, so that I can more easily build social contacts and develop personally.
6. **Real-Time Map:** As someone who feels lonely (e.g., Karin or Tom), I want to see on a safe real-time map where activities, quests, and potential meetup opportunities are in my area, so that I can spontaneously participate in social events.
7. **Playful Progress Tracking:** As a user, I want to collect virtual decoration objects, badges, and digital currency through challenges, regular participation, social interactions, and optionally through in-app purchases, so that I remain motivated and experience my progress in a playful way.
8. **Social Progress:** As a user, I want to track my social progress (e.g., new contacts, meetups, group quests, friends), so that I can see how I'm developing socially and stay motivated.

### Technical User Stories

1. **Participation Verification:** As a user, I want to easily and securely confirm my participation in quests or meetups through a simple digital process, so that I can ensure rewards are only given for genuine, on-site activities.
2. **Anonymous Usage:** As a privacy-conscious person, I want to anonymize my location and profile from other users and businesses, so that I can use the app without feeling uncomfortable or unsafe.

### Business User Stories

1. **Promotion & Marketing:** As a local business, I want to create my own challenges, quests, or events in the app and place local recommendations (e.g., café, store, event) through a transparent auction process, so that I can launch campaigns that motivate users to visit my business or events.
2. **In-App Currency Distribution:** As a local business, I want to acquire digital in-app currency through a simple purchase process identical to private users, so that I can distribute it as rewards for my challenges and events to users.

### Addendum to User Stories During Development

1. **Authentication:** User registration and login.

2. **Profile View and Management:** As a user, I want to view my own profile with my interests, social battery index, digital currency, and experience points, and be able to adjust settings, so that I can tailor my app usage to my needs.

## MVP

- Open local list of challenges including challenge filter to enable spontaneous participation. _(User Story #2, Persona: Tom, Karin)_
<!-- - Real-time map with personal location marker and dynamic challenges (coordinates, nearest address, interest keyword, name, description, date, topics/interests (e.g., café, politics, ...), start time, estimated end time, experience points, digital currency amount, social effort). _(User Story #6, Persona: Karin, Tom)_ -->
<!-- - Event/Challenge creation via API by business profiles including interest keywords. _(User Story #11, #12)_ -->
<!-- - Social Battery input field with visual status and filter logic for suitable activity suggestions. _(User Story #1, Persona: Karin, Tom)_ -->
- Interest matching via selectable interest keywords from a predefined list, combined with challenge filter. _(User Story #3, Persona: Lena, Tom, Marcel)_
<!-- - Participation verification via [QR code and] 5-digit code (challenge-specific) including on-site check-in. _(User Story #9)_ -->
- Reward system with experience points and digital currency for completed activities. _(User Story #7, Persona: Lena, Marcel)_
- AI chatbot message upon app login with challenge suggestion based on interest matching. _(User Story #5, #3)_
