# Kinetiq: 1-Minute App Promotional Video Blueprint & Script

> **App Concept:** Kinetiq – Next-Gen AI Fitness, Nutrition & Computer Vision Health Ecosystem  
> **Target Duration:** 60 Seconds (1 Minute)  
> **Visual Vibe:** High-energy, futuristic, sleek dark mode (`#0D0D0D`) with electric cyan/lime highlights (`#00F0FF` / `#D0FF00`), fast-paced cuts, glowing AI overlays, and rhythmic motion graphics.  
> **Audio Vibe:** Modern synthwave / energetic cinematic trap beat with futuristic UI sound effects (whooshes, camera shutter, AI scanning pings, rep count chimes).  

---

## 🚀 Video Overview & Key Angles

| Hook / Theme | Description | Featured Feature |
| :--- | :--- | :--- |
| **0:00 - 0:10** | **The Hook** | Daily friction of manual fitness tracking vs. AI automation |
| **0:10 - 0:24** | **Computer Vision AI** | Real-time Pose Detection, Skeleton Overlay & Voice Rep Counting |
| **0:24 - 0:38** | **Gemini AI Nutrition** | AI Camera Meal Scanner, Instant Calorie & Macro Breakdown |
| **0:38 - 0:48** | **Ecosystem & Recovery** | 24/7 AI Coach, Readiness Score, Workout Generator & Plans |
| **0:48 - 1:00** | **Call To Action (CTA)** | High-impact finish, Kinetiq logo reveal, App Download CTA |

---

## 🎬 Second-by-Second Storyboard & Shot List

```text
[0:00 - 0:05] --- INTRO HOOK
Visual: Dynamic dark screen with pulsing electric cyan circuit lines. Rapid montage of a user struggling to count reps and manually log food on a bland paper/old app. High contrast transition flash into Kinetiq splash screen with glowing neon cyan/lime logo.
Text Overlay: "FITNESS JUST EVOLVED."
SFX: Heavy bass drop + futuristic charging noise.
VO: "Stop guessing your workouts and manually counting your calories."

[0:05 - 0:10] --- DASHBOARD & READINESS
Visual: Smooth zoom-in to Kinetiq Home Screen showing Today's Progress ring, Recovery Readiness Score (92%), Hydration Tracker, and Streak counter ("🔥 7 DAY STREAK").
Text Overlay: "YOUR COMPLETE HEALTH HUB"
SFX: Clean UI tap sounds, soft glass swipe sound.
VO: "Meet Kinetiq – your all-in-one AI fitness and health engine."

[0:10 - 0:24] --- VISION AI (POSE TRACKING & REP COUNTER)
Visual: Camera cuts to a phone mounted on a tripod recording a squat workout. Vision AI activates instantly! Green/cyan glowing skeletal overlay tracks the user's joints in real-time. Rep counter increments dynamically (1... 2... 3!) with instant audio form cue "Great form! Squat depth reached."
Text Overlay: "KINETIQ VISION AI" | "REAL-TIME REP & FORM TRACKER"
SFX: Camera shutter click + futuristic scanning laser sweep + ping on rep count.
VO: "Turn your smartphone camera into a personal trainer. Real-time computer vision counts your reps and fixes your form on the spot."

[0:24 - 0:38] --- AI MEAL SCANNER & NUTRITION
Visual: Phone camera points at a plate with grilled chicken, quinoa, and avocado. User taps "ANALYZE MEAL WITH AI". Glowing scanning beam sweeps over the plate. Instantly, a sleek card pops up with "650 KCAL", 48g Protein, 52g Carbs, 18g Fats, detected ingredients, and log button.
Text Overlay: "GEMINI 1.5 MEAL SCANNER" | "INSTANT MACRO RECOGNITION"
SFX: High-tech digital scanning sound + success chime.
VO: "Nutrition simplified. Point your camera at any meal for instant calorie and macronutrient breakdowns in seconds."

[0:38 - 0:48] --- 24/7 AI COACH & WORKOUT GENERATOR
Visual: Rapid split-screen showcase:
1. AICoachScreen chatting with Gemini AI ("Adjust today's workout for shoulder recovery").
2. WorkoutGeneratorScreen creating a custom intermediate Chest & Triceps routine.
3. Specialized Plans (Powerlifting, Athlete, Fat Loss Shred).
Text Overlay: "INTELLIGENT WORKOUT GENERATOR" | "24/7 AI PERSONAL COACH"
SFX: Fast typing SFX + energetic synth rise.
VO: "Need a custom routine or expert advice? Your 24/7 AI Coach builds personalized programs tailored to your goals."

[0:48 - 1:00] --- OUTRO & CALL TO ACTION
Visual: Fast-paced recap carousel showing all bottom nav icons (Home, Workout, Recipes, Vision AI, Coach, Profile). Screen pulses in sync with music beat, settling on a high-gloss 3D Kinetiq emblem.
Text Overlay: "KINETIQ" | "THE FUTURE OF FITNESS IS HERE" | "DOWNLOAD NOW"
SFX: Epic cinematic riser + final punchy kick beat.
VO: "Level up your performance today. Download Kinetiq now and take control of your fitness journey."
```

---

## 🎙️ Complete Voiceover (VO) Script

> **Tone:** Energetic, confident, premium, tech-forward.  
> **Pacing:** Upbeat and punchy (135 - 140 words per minute).  

```text
[0:00 - 0:05]
Stop guessing your workouts and manually counting calories. Fitness just evolved.

[0:05 - 0:10]
Meet Kinetiq – your all-in-one AI fitness and health engine.

[0:10 - 0:24]
Turn your phone camera into a personal coach. With Kinetiq Vision AI, real-time pose tracking automatically counts your reps and guides your form with instant audio feedback.

[0:24 - 0:38]
Fueling your body has never been easier. Snap a photo of any meal, and our Gemini AI instantly breaks down your calories, protein, and macros with precision.

[0:38 - 0:48]
Stay ahead with daily recovery readiness scores, personalized workout generators, and a 24/7 AI coach ready to answer every fitness question.

[0:48 - 1:00]
No hassle. Just real results. Download Kinetiq today and unleash your peak potential!
```

---

## 📋 ADB Command to Record Video

Run this command directly in your terminal to record 60fps HD screen recordings from your Android device/emulator:

```bash
# Record 1080p video directly on device
adb shell screenrecord --bit-rate 16000000 --size 1080x2400 /sdcard/kinetiq_promo_raw.mp4

# Pull recorded video file to your computer
adb pull /sdcard/kinetiq_promo_raw.mp4 ./kinetiq_promo_raw.mp4
```
