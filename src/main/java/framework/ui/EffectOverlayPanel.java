package framework.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Transparent overlay painted on top of the whole window (set as the frame's glass
 * pane). Plays short-lived win celebration effects and never intercepts mouse input,
 * even while an effect is running.
 */
public final class EffectOverlayPanel extends JComponent {

    private static final int EFFECT_DURATION_MS = 3000;
    private static final int FRAME_INTERVAL_MS = 16;
    private static final int CONFETTI_PIECE_COUNT = 150;
    private static final int FIREWORK_COUNT = 4;
    private static final long FIREWORK_SPAWN_STAGGER_MS = 400L;
    private static final int MARK_COUNT = 24;
    private static final Color[] CONFETTI_COLORS = {
        new Color(0xE74C3C), new Color(0xF1C40F), new Color(0x2ECC71),
        new Color(0x3498DB), new Color(0x9B59B6), new Color(0xE67E22)
    };
    private static final Color[] FIREWORK_COLORS = {
        new Color(0xFF6B6B), new Color(0xFFD93D), new Color(0x6BCB77), new Color(0x4D96FF)
    };

    private final Random random = new Random();
    private final ConfettiEffect confettiEffect = new ConfettiEffect(random);
    private final FireworksEffect fireworksEffect = new FireworksEffect(random);
    private final MarksEffect marksEffect = new MarksEffect(random);
    private final List<WinEffect> effects = List.of(confettiEffect, fireworksEffect, marksEffect);

    private Timer animationTimer;

    public EffectOverlayPanel() {
        setOpaque(false);
    }

    @Override
    public boolean contains(int x, int y) {
        return false;
    }

    public void playConfetti() {
        play(confettiEffect);
    }

    public void playFireworks() {
        play(fireworksEffect);
    }

    public void playMarks() {
        play(marksEffect);
    }

    private void play(WinEffect effect) {
        effect.play(Math.max(getWidth(), 1), Math.max(getHeight(), 1));
        startAnimating();
    }

    private void startAnimating() {
        if (animationTimer == null) {
            animationTimer = new Timer(FRAME_INTERVAL_MS, e -> tick());
        }
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    private void tick() {
        long now = System.currentTimeMillis();
        effects.forEach(effect -> effect.tick(now));

        repaint();

        if (effects.stream().noneMatch(WinEffect::isActive)) {
            animationTimer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        long now = System.currentTimeMillis();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        effects.forEach(effect -> effect.paint(g2, now));
        g2.dispose();
    }

    /**
     * Template Method: every win effect spawns particles once, advances and checks
     * expiry on each tick, and paints while active. Subclasses only supply how to do
     * each of those steps for their own kind of particle.
     */
    private abstract static class WinEffect {

        private boolean active;

        final void play(int width, int height) {
            spawnParticles(width, height);
            active = true;
        }

        final void tick(long now) {
            if (!active) {
                return;
            }
            advance(now);
            if (isExpired(now)) {
                clearParticles();
                active = false;
            }
        }

        final boolean isActive() {
            return active;
        }

        final void paint(Graphics2D g2, long now) {
            if (active) {
                paintParticles(g2, now);
            }
        }

        abstract void spawnParticles(int width, int height);

        abstract void advance(long now);

        abstract boolean isExpired(long now);

        abstract void paintParticles(Graphics2D g2, long now);

        abstract void clearParticles();
    }

    private static final class ConfettiEffect extends WinEffect {

        private final Random random;
        private final List<ConfettiPiece> pieces = new ArrayList<>();

        ConfettiEffect(Random random) {
            this.random = random;
        }

        @Override
        void spawnParticles(int width, int height) {
            pieces.clear();
            for (int i = 0; i < CONFETTI_PIECE_COUNT; i++) {
                pieces.add(new ConfettiPiece(random, width));
            }
        }

        @Override
        void advance(long now) {
            pieces.forEach(ConfettiPiece::advance);
            pieces.removeIf(ConfettiPiece::isExpired);
        }

        @Override
        boolean isExpired(long now) {
            return pieces.isEmpty();
        }

        @Override
        void paintParticles(Graphics2D g2, long now) {
            pieces.forEach(piece -> piece.paint(g2));
        }

        @Override
        void clearParticles() {
            pieces.clear();
        }
    }

    private static final class FireworksEffect extends WinEffect {

        private final Random random;
        private final List<Firework> fireworks = new ArrayList<>();

        FireworksEffect(Random random) {
            this.random = random;
        }

        @Override
        void spawnParticles(int width, int height) {
            fireworks.clear();
            long now = System.currentTimeMillis();
            for (int i = 0; i < FIREWORK_COUNT; i++) {
                fireworks.add(new Firework(random, width, height, now + i * FIREWORK_SPAWN_STAGGER_MS));
            }
        }

        @Override
        void advance(long now) {
            fireworks.forEach(firework -> firework.advance(now));
            fireworks.removeIf(firework -> firework.isExpired(now));
        }

        @Override
        boolean isExpired(long now) {
            return fireworks.isEmpty();
        }

        @Override
        void paintParticles(Graphics2D g2, long now) {
            fireworks.forEach(firework -> firework.paint(g2, now));
        }

        @Override
        void clearParticles() {
            fireworks.clear();
        }
    }

    private static final class MarksEffect extends WinEffect {

        private final Random random;
        private final List<SymbolMark> marks = new ArrayList<>();
        private long startedAtMs = -1;

        MarksEffect(Random random) {
            this.random = random;
        }

        @Override
        void spawnParticles(int width, int height) {
            marks.clear();
            long spawnWindowMs = Math.max(EFFECT_DURATION_MS - SymbolMark.CYCLE_MS, 0);
            for (int i = 0; i < MARK_COUNT; i++) {
                marks.add(new SymbolMark(random, width, height, spawnWindowMs));
            }
            startedAtMs = System.currentTimeMillis();
        }

        @Override
        void advance(long now) {
            // marks fade purely as a function of elapsed time; nothing to mutate per tick
        }

        @Override
        boolean isExpired(long now) {
            return now - startedAtMs > EFFECT_DURATION_MS;
        }

        @Override
        void paintParticles(Graphics2D g2, long now) {
            long elapsed = now - startedAtMs;
            marks.forEach(mark -> mark.paint(g2, elapsed));
        }

        @Override
        void clearParticles() {
            marks.clear();
            startedAtMs = -1;
        }
    }

    private static final class ConfettiPiece {

        private static final int SPAWN_ABOVE_PANEL_OFFSET = 20;
        private static final int SPAWN_ABOVE_PANEL_RANGE = 200;
        private static final double HORIZONTAL_DRIFT_RANGE = 2;
        private static final double FALL_SPEED_MIN = 2;
        private static final double FALL_SPEED_RANGE = 3;
        private static final double ROTATION_SPEED_RANGE = 10;
        private static final double GRAVITY = 0.05;
        private static final int SIZE_MIN = 6;
        private static final int SIZE_RANGE = 6;

        private double x;
        private double y;
        private final double vx;
        private double vy;
        private double rotation;
        private final double rotationSpeed;
        private final Color color;
        private final int size;
        private int framesLived;

        ConfettiPiece(Random random, int panelWidth) {
            this.x = random.nextDouble() * panelWidth;
            this.y = -SPAWN_ABOVE_PANEL_OFFSET - random.nextInt(SPAWN_ABOVE_PANEL_RANGE);
            this.vx = random.nextDouble() * HORIZONTAL_DRIFT_RANGE - HORIZONTAL_DRIFT_RANGE / 2;
            this.vy = FALL_SPEED_MIN + random.nextDouble() * FALL_SPEED_RANGE;
            this.rotation = random.nextDouble() * 360;
            this.rotationSpeed = random.nextDouble() * ROTATION_SPEED_RANGE - ROTATION_SPEED_RANGE / 2;
            this.color = CONFETTI_COLORS[random.nextInt(CONFETTI_COLORS.length)];
            this.size = SIZE_MIN + random.nextInt(SIZE_RANGE);
        }

        void advance() {
            x += vx;
            y += vy;
            vy += GRAVITY;
            rotation += rotationSpeed;
            framesLived++;
        }

        boolean isExpired() {
            return framesLived > EFFECT_DURATION_MS / FRAME_INTERVAL_MS;
        }

        void paint(Graphics2D g2) {
            Graphics2D piece = (Graphics2D) g2.create();
            piece.translate(x, y);
            piece.rotate(Math.toRadians(rotation));
            piece.setColor(color);
            piece.fillRect(-size / 2, -size / 2, size, size / 2);
            piece.dispose();
        }
    }

    private static final class Firework {

        private static final int PARTICLE_COUNT = 40;
        private static final long BURST_LIFETIME_MS = 900;
        private static final int EDGE_MARGIN = 40;
        private static final double PARTICLE_SPEED_MIN = 1.5;
        private static final double PARTICLE_SPEED_RANGE = 2.5;
        private static final double GRAVITY = 0.06;
        private static final int PARTICLE_RADIUS = 2;

        private final long spawnAtMs;
        private final int centerX;
        private final int centerY;
        private final Color color;
        private final List<Particle> particles = new ArrayList<>(PARTICLE_COUNT);
        private boolean exploded;
        private long explodedAtMs;

        Firework(Random random, int panelWidth, int panelHeight, long spawnAtMs) {
            this.spawnAtMs = spawnAtMs;
            this.centerX = EDGE_MARGIN + random.nextInt(Math.max(panelWidth - EDGE_MARGIN * 2, 1));
            this.centerY = EDGE_MARGIN + random.nextInt(Math.max(panelHeight / 2, 1));
            this.color = FIREWORK_COLORS[random.nextInt(FIREWORK_COLORS.length)];
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                double angle = 2 * Math.PI * i / PARTICLE_COUNT;
                double speed = PARTICLE_SPEED_MIN + random.nextDouble() * PARTICLE_SPEED_RANGE;
                particles.add(new Particle(Math.cos(angle) * speed, Math.sin(angle) * speed));
            }
        }

        void advance(long now) {
            if (!exploded) {
                if (now < spawnAtMs) {
                    return;
                }
                exploded = true;
                explodedAtMs = now;
            }
            for (Particle particle : particles) {
                particle.x += particle.vx;
                particle.y += particle.vy;
                particle.vy += GRAVITY;
            }
        }

        boolean isExpired(long now) {
            return exploded && now - explodedAtMs > BURST_LIFETIME_MS;
        }

        void paint(Graphics2D g2, long now) {
            if (!exploded) {
                return;
            }
            long elapsed = now - explodedAtMs;
            float alpha = Math.max(0f, 1f - elapsed / (float) BURST_LIFETIME_MS);
            if (alpha <= 0f) {
                return;
            }
            Graphics2D burst = (Graphics2D) g2.create();
            burst.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            burst.setColor(color);
            for (Particle particle : particles) {
                int px = centerX + (int) particle.x;
                int py = centerY + (int) particle.y;
                burst.fillOval(px - PARTICLE_RADIUS, py - PARTICLE_RADIUS,
                    PARTICLE_RADIUS * 2, PARTICLE_RADIUS * 2);
            }
            burst.dispose();
        }

        private static final class Particle {
            double x;
            double y;
            final double vx;
            double vy;

            Particle(double vx, double vy) {
                this.vx = vx;
                this.vy = vy;
            }
        }
    }

    private static final class SymbolMark {

        private static final long FADE_MS = 250;
        private static final long HOLD_MS = 350;
        private static final long CYCLE_MS = FADE_MS * 2 + HOLD_MS;
        private static final int EDGE_MARGIN = 16;
        private static final float FONT_SIZE = 20f;

        private final int x;
        private final int y;
        private final String glyph;
        private final Color color;
        private final long appearAtMs;

        SymbolMark(Random random, int panelWidth, int panelHeight, long maxAppearDelayMs) {
            this.x = EDGE_MARGIN + random.nextInt(Math.max(panelWidth - EDGE_MARGIN * 2, 1));
            this.y = EDGE_MARGIN + random.nextInt(Math.max(panelHeight - EDGE_MARGIN * 2, 1));
            this.glyph = random.nextBoolean() ? "X" : "O";
            this.color = CONFETTI_COLORS[random.nextInt(CONFETTI_COLORS.length)];
            this.appearAtMs = (long) (random.nextDouble() * maxAppearDelayMs);
        }

        void paint(Graphics2D g2, long elapsedMs) {
            float alpha = alphaAt(elapsedMs);
            if (alpha <= 0f) {
                return;
            }
            Graphics2D mark = (Graphics2D) g2.create();
            mark.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            mark.setColor(color);
            mark.setFont(mark.getFont().deriveFont(Font.BOLD, FONT_SIZE));
            mark.drawString(glyph, x, y);
            mark.dispose();
        }

        private float alphaAt(long elapsedMs) {
            long t = elapsedMs - appearAtMs;
            if (t < 0 || t > CYCLE_MS) {
                return 0f;
            }
            if (t < FADE_MS) {
                return t / (float) FADE_MS;
            }
            if (t > FADE_MS + HOLD_MS) {
                return (CYCLE_MS - t) / (float) FADE_MS;
            }
            return 1f;
        }
    }
}
