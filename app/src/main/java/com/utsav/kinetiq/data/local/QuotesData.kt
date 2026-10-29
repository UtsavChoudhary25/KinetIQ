package com.utsav.kinetiq.data.local

import com.utsav.kinetiq.domain.model.Quote
import com.utsav.kinetiq.domain.model.QuoteCategory

object QuotesData {
    val quotes: List<Quote> = listOf(
        // 🥇 Top Legendary Quotes
        Quote("The future depends on what you do today.", "Mahatma Gandhi", QuoteCategory.CONSISTENCY),
        Quote("Strength does not come from physical capacity. It comes from an indomitable will.", "Mahatma Gandhi", QuoteCategory.DISCIPLINE),
        Quote("It always seems impossible until it’s done.", "Nelson Mandela", QuoteCategory.MINDSET),
        Quote("Do what you can, with what you have, where you are.", "Theodore Roosevelt", QuoteCategory.DISCIPLINE),
        Quote("Believe you can and you’re halfway there.", "Theodore Roosevelt", QuoteCategory.MINDSET),
        Quote("The greatest wealth is health.", "Virgil", QuoteCategory.PERFORMANCE),
        Quote("We are what we repeatedly do. Excellence, then, is not an act, but a habit.", "Aristotle", QuoteCategory.CONSISTENCY),
        Quote("He who conquers others is strong; he who conquers himself is mighty.", "Lao Tzu", QuoteCategory.MINDSET),
        Quote("A journey of a thousand miles begins with a single step.", "Lao Tzu", QuoteCategory.CONSISTENCY),
        Quote("You have power over your mind—not outside events. Realize this, and you will find strength.", "Marcus Aurelius", QuoteCategory.MINDSET),

        // 🐉 Bruce Lee
        Quote("Knowing is not enough, we must apply. Willing is not enough, we must do.", "Bruce Lee", QuoteCategory.DISCIPLINE),
        Quote("Absorb what is useful, discard what is useless and add what is specifically your own.", "Bruce Lee", QuoteCategory.MINDSET),
        Quote("Be water, my friend.", "Bruce Lee", QuoteCategory.MINDSET),
        Quote("The successful warrior is the average man, with laser-like focus.", "Bruce Lee", QuoteCategory.PERFORMANCE),
        Quote("A goal is not always meant to be reached, it often serves simply as something to aim at.", "Bruce Lee", QuoteCategory.CONSISTENCY),
        Quote("Mistakes are always forgivable, if one has the courage to admit them.", "Bruce Lee", QuoteCategory.MINDSET),
        Quote("The key to immortality is first living a life worth remembering.", "Bruce Lee", QuoteCategory.PERFORMANCE),
        Quote("If you spend too much time thinking about a thing, you’ll never get it done.", "Bruce Lee", QuoteCategory.DISCIPLINE),

        // 🥊 Muhammad Ali
        Quote("Don’t count the days; make the days count.", "Muhammad Ali", QuoteCategory.CONSISTENCY),
        Quote("He who is not courageous enough to take risks will accomplish nothing in life.", "Muhammad Ali", QuoteCategory.PERFORMANCE),
        Quote("I hated every minute of training, but I said, ‘Don’t quit.’", "Muhammad Ali", QuoteCategory.DISCIPLINE),
        Quote("It’s not bragging if you can back it up.", "Muhammad Ali", QuoteCategory.PERFORMANCE),
        Quote("The will must be stronger than the skill.", "Muhammad Ali", QuoteCategory.MINDSET),

        // 🏋️ Arnold Schwarzenegger
        Quote("The resistance that you fight physically in the gym and the resistance that you fight in life can only build a strong character.", "Arnold Schwarzenegger", QuoteCategory.DISCIPLINE),
        Quote("The worst thing I can be is the same as everybody else.", "Arnold Schwarzenegger", QuoteCategory.MINDSET),
        Quote("You have to build the ultimate physical machine, but also the ultimate mind.", "Arnold Schwarzenegger", QuoteCategory.DISCIPLINE),
        Quote("The pain you feel today will be the strength you feel tomorrow.", "Arnold Schwarzenegger", QuoteCategory.DISCIPLINE),
        Quote("Training gives us an outlet for suppressed energies created by stress.", "Arnold Schwarzenegger", QuoteCategory.PERFORMANCE),

        // 🏀 Michael Jordan
        Quote("You must expect great things of yourself before you can do them.", "Michael Jordan", QuoteCategory.MINDSET),
        Quote("Some people want it to happen, some wish it would happen, others make it happen.", "Michael Jordan", QuoteCategory.CONSISTENCY),
        Quote("Limits, like fears, are often just an illusion.", "Michael Jordan", QuoteCategory.MINDSET),
        Quote("I’ve failed over and over and over again in my life. And that is why I succeed.", "Michael Jordan", QuoteCategory.PERFORMANCE),

        // 🏆 Kobe Bryant
        Quote("The most important thing is to try and inspire people so that they can be great in whatever they want to do.", "Kobe Bryant", QuoteCategory.PERFORMANCE),
        Quote("Everything negative—pressure, challenges—is all an opportunity for me to rise.", "Kobe Bryant", QuoteCategory.MINDSET),
        Quote("The moment you give up is the moment you let someone else win.", "Kobe Bryant", QuoteCategory.DISCIPLINE),

        // 🔥 David Goggins
        Quote("You are in danger of living a life so comfortable and soft that you will die without ever realizing your true potential.", "David Goggins", QuoteCategory.MINDSET),
        Quote("Don’t stop when you’re tired. Stop when you’re done.", "David Goggins", QuoteCategory.DISCIPLINE),
        Quote("Suffering is a test. That’s all it is.", "David Goggins", QuoteCategory.DISCIPLINE),
        Quote("The most important conversations you’ll ever have are the ones you’ll have with yourself.", "David Goggins", QuoteCategory.MINDSET),

        // 🧠 Discipline, Mindset & Performance
        Quote("Success is walking from failure to failure with no loss of enthusiasm.", "Winston Churchill", QuoteCategory.CONSISTENCY),
        Quote("The secret of getting ahead is getting started.", "Mark Twain", QuoteCategory.CONSISTENCY),
        Quote("The only way to do great work is to love what you do.", "Steve Jobs", QuoteCategory.PERFORMANCE),
        Quote("The harder the battle, the sweeter the victory.", "Les Brown", QuoteCategory.PERFORMANCE),
        Quote("Discipline is doing what you hate to do, but nonetheless doing it like you love it.", "Mike Tyson", QuoteCategory.DISCIPLINE),
        Quote("Everyone has a plan until they get punched in the mouth.", "Mike Tyson", QuoteCategory.MINDSET),
        Quote("If something stands between you and your success, move it. Never be denied.", "Dwayne Johnson", QuoteCategory.DISCIPLINE),
        Quote("Success isn’t always about greatness. It’s about consistency.", "Dwayne Johnson", QuoteCategory.CONSISTENCY),
        Quote("You miss 100% of the shots you don’t take.", "Wayne Gretzky", QuoteCategory.PERFORMANCE),
        Quote("No man is free who is not master of himself.", "Epictetus", QuoteCategory.DISCIPLINE),
        Quote("Waste no more time arguing what a good man should be. Be one.", "Marcus Aurelius", QuoteCategory.MINDSET)
    )
}
