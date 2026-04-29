// 경로: src/main/java/com/akashiro/tails/client/gui/controls/GuiTintPanel.java
package com.akashiro.tails.client.gui.controls;

import com.akashiro.tails.client.gui.GuiTailsEditor;
import com.akashiro.tails.common.data.PartInfo;
import com.akashiro.tails.common.data.PartsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * 우측 틴트 패널 — 원본 1.12.2 UI 재현.
 *
 * 상단: Tint 1 / Tint 2 / Tint 3 각각
 *   [색상 미리보기 박스] [Edit] 버튼
 *
 * Edit 클릭 시 하단에:
 *   "Editing Tint N"
 *   Hex: [입력창]
 *   R ━━━━━━━━━ 213.0
 *   G ━━━━━━━━━ 188.0
 *   B ━━━━━━━━━ 255.0
 */
public class GuiTintPanel {


    // ── 9가지 퀵 컬러 상수 ─────────────────────────
    private static final int[] QUICK_COLORS = {
        0xFFFFFF, // 하얀색
        0x000000, // 검정색
        0xFF0000, // 빨강
        0xFF7700, // 주황
        0xFFFF00, // 노랑
        0x00CC00, // 초록
        0x0000FF, // 파랑
        0x000088, // 남색
        0x8800CC, // 보라
    };
    private static final String[] QUICK_COLOR_NAMES = {
        "White","Black","Red","Orange","Yellow","Green","Blue","Navy","Purple"
    };

    private static final int BOX_SIZE    = 20;
    private static final int SLIDER_H    = 10;
    private static final int SLIDER_GAP  = 4;
    private static final int ROW_H       = 28;

    private final GuiTailsEditor parent;
    private final int x, y, w, h;

    private int[]  tints        = {0xFFFFFF, 0xFFFFFF, 0xFFFFFF};
    private int    editingTint  = -1;   // 현재 편집 중인 틴트 인덱스 (-1=없음)
    private int    draggingCh   = -1;   // 드래그 중인 채널 (0=R,1=G,2=B) or -1

    // Hex 입력
    private String hexInput     = "";
    private boolean hexFocused  = false;

    public GuiTintPanel(GuiTailsEditor parent, int x, int y, int w, int h) {
        this.parent = parent;
        this.x = x; this.y = y; this.w = w; this.h = h;
    }

    public void init() { syncFromPartInfo(); }

    public void onPartTypeChanged(PartsData.PartType type) {
        editingTint = -1;
        syncFromPartInfo();
    }

    private void syncFromPartInfo() {
        PartInfo info = parent.getCurrentPartInfo();
        if (info != null && info.hasPart && info.tints != null && info.tints.length == 3) {
            tints = info.tints.clone();
        } else {
            tints = new int[]{0xFFFFFF, 0xFFFFFF, 0xFFFFFF};
        }
        if (editingTint >= 0) hexInput = toHex(tints[editingTint]);
    }

    // ── 렌더 ──────────────────────────────────────
    public void render(GuiGraphics g, int mx, int my, float pt) {
        var font = Minecraft.getInstance().font;

        // Tint 1 / 2 / 3 행
        for (int i = 0; i < 3; i++) {
            int ry = y + 4 + i * ROW_H;

            // 라벨
            g.drawString(font,
                    Component.translatable("gui.tint", i + 1),
                    x + 4, ry + 6, 0xFFFFFF, false);

            // 색상 미리보기 박스
            int boxX = x + 50;
            g.fill(boxX,     ry,              boxX + BOX_SIZE, ry + BOX_SIZE,
                    0xFF000000 | tints[i]);
            g.renderOutline(boxX, ry, BOX_SIZE, BOX_SIZE, 0xFF888888);

            // Edit 버튼 (클릭 감지는 mouseClicked에서)
            boolean editing  = (editingTint == i);
            int btnX = boxX + BOX_SIZE + 2;
            int btnCol = editing ? 0xFF5577AA : 0xFF444444;
            g.fill(btnX, ry, btnX + 32, ry + BOX_SIZE, btnCol);
            g.renderOutline(btnX, ry, 32, BOX_SIZE, 0xFF888888);
            g.drawCenteredString(font,
                    Component.translatable("gui.button.edit"),
                    btnX + 16, ry + 6, 0xFFFFFF);
        }

        // 편집 중인 틴트 슬라이더
        if (editingTint >= 0) {
            renderEditArea(g, mx, my);
        }
    }

    private void renderEditArea(GuiGraphics g, int mx, int my) {
        var font = Minecraft.getInstance().font;
        int baseY = y + 4 + 3 * ROW_H + 8;

        // "Editing Tint N" 라벨
        g.drawString(font,
                Component.translatable("gui.tint.edit", editingTint + 1),
                x + 4, baseY, 0xFFFFFF, false);
        baseY += 12;

        // Hex 입력 배경
        int hexBoxX = x + 30;
        int hexBoxW = w - 34;
        g.fill(hexBoxX, baseY, hexBoxX + hexBoxW, baseY + 12,
                hexFocused ? 0xFF222244 : 0xFF222222);
        g.renderOutline(hexBoxX, baseY, hexBoxW, 12,
                hexFocused ? 0xFF8899CC : 0xFF555555);
        g.drawString(font, Component.translatable("gui.hex").getString() + ": " + hexInput + (hexFocused ? "_" : ""),
                x + 4, baseY + 2, 0xFFFFFF, false);
        baseY += 18;

        // R / G / B 슬라이더
        int r = (tints[editingTint] >> 16) & 0xFF;
        int gv= (tints[editingTint] >>  8) & 0xFF;
        int b =  tints[editingTint]        & 0xFF;

        drawSlider(g, mx, my, baseY,                          "R", r, 0xFFCC3333, 0);
        drawSlider(g, mx, my, baseY + SLIDER_H + SLIDER_GAP, "G", gv,0xFF33CC33, 1);
        drawSlider(g, mx, my, baseY + (SLIDER_H + SLIDER_GAP)*2, "B", b,0xFF3333CC,2);

        // ── 퀵 컬러 버튼 9개 ──────────────────────────
        int qBase = baseY + (SLIDER_H + SLIDER_GAP) * 3 + 10;
        g.drawString(Minecraft.getInstance().font,
                Component.translatable("gui.quickcolors"), x + 4, qBase, 0xAAAAAA, false);
        qBase += 11;
        int btnSz = (w - 8) / 9;
        for (int qi = 0; qi < QUICK_COLORS.length; qi++) {
            int qx = x + 4 + qi * btnSz;
            int col = QUICK_COLORS[qi];
            g.fill(qx, qBase, qx + btnSz - 1, qBase + btnSz - 1, 0xFF000000 | col);
            g.renderOutline(qx, qBase, btnSz - 1, btnSz - 1, 0xFF888888);
        }

    }

    private void drawSlider(GuiGraphics g, int mx, int my,
                            int sy, String label, int value, int color, int ch) {
        var font  = Minecraft.getInstance().font;
        int sx    = x + 14;
        int sw    = w - 50;

        // 트랙 배경
        g.fill(sx, sy, sx + sw, sy + SLIDER_H, 0xFF333333);

        // 채워진 바
        int fillW = (int)(value / 255.0 * sw);
        g.fill(sx, sy, sx + fillW, sy + SLIDER_H, color);

        // 핸들
        int hx = sx + fillW - 2;
        g.fill(hx, sy - 1, hx + 4, sy + SLIDER_H + 1, 0xFFFFFFFF);

        // 채널 라벨
        g.drawString(font, label, x + 4, sy + 1, 0xFFFFFF, false);

        // 값
        g.drawString(font, String.valueOf(value),
                sx + sw + 3, sy + 1, 0xAAAAAA, false);
    }

    // ── 마우스 이벤트 ─────────────────────────────
    public void mouseClicked(double mx, double my, int btn) {
        if (mx < x || mx > x + w) return;

        // 퀵 컬러 클릭
        if (editingTint >= 0) {
            int qBase2 = y + 4 + 3 * ROW_H + 20 + 18 + (SLIDER_H + SLIDER_GAP) * 3 + 21;
            int btnSz2 = (w - 8) / 9;
            if (my >= qBase2 && my < qBase2 + btnSz2) {
                for (int qi = 0; qi < QUICK_COLORS.length; qi++) {
                    int qx = x + 4 + qi * btnSz2;
                    if (mx >= qx && mx < qx + btnSz2) {
                        tints[editingTint] = QUICK_COLORS[qi];
                        hexInput = toHex(tints[editingTint]);
                        pushToPartInfo();
                        return;
                    }
                }
            }
        }


        // Edit 버튼 클릭
        for (int i = 0; i < 3; i++) {
            int ry   = y + 4 + i * ROW_H;
            int boxX = x + 50;
            int btnX = boxX + BOX_SIZE + 2;
            if (mx >= btnX && mx <= btnX + 32 && my >= ry && my <= ry + BOX_SIZE) {
                editingTint = (editingTint == i) ? -1 : i;
                if (editingTint >= 0) hexInput = toHex(tints[editingTint]);
                hexFocused = false;
                return;
            }
        }

        // Hex 입력 박스 클릭
        if (editingTint >= 0) {
            int baseY = y + 4 + 3 * ROW_H + 20;
            int hexBoxX = x + 30;
            int hexBoxW = w - 34;
            if (mx >= hexBoxX && mx <= hexBoxX + hexBoxW
                    && my >= baseY && my <= baseY + 12) {
                hexFocused = true;
                return;
            } else {
                hexFocused = false;
            }

            // 슬라이더 클릭
            int sliderBase = baseY + 18;
            draggingCh = getSliderChannel((int)my, sliderBase);
            if (draggingCh >= 0) updateSlider((int)mx, draggingCh);
        }
    }

    public void mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (draggingCh >= 0 && editingTint >= 0) {
            updateSlider((int)mx, draggingCh);
        }
    }

    public void mouseReleased(double mx, double my, int btn) {
        draggingCh = -1;
    }

    // ── 키 입력 (Hex 입력) ────────────────────────
    public boolean keyPressed(int key, int scan, int mod) {
        if (!hexFocused || editingTint < 0) return false;

        // Backspace
        if (key == 259 && !hexInput.isEmpty()) {
            hexInput = hexInput.substring(0, hexInput.length() - 1);
            return true;
        }
        // Enter → 적용
        if (key == 257) {
            applyHex();
            hexFocused = false;
            return true;
        }
        return false;
    }

    public boolean charTyped(char c, int mod) {
        if (!hexFocused || editingTint < 0) return false;
        if (hexInput.length() < 6 && isHexChar(c)) {
            hexInput += c;
            if (hexInput.length() == 6) applyHex();
            return true;
        }
        return false;
    }

    // ── 내부 유틸 ─────────────────────────────────
    private int getSliderChannel(int my, int sliderBase) {
        for (int ch = 0; ch < 3; ch++) {
            int sy = sliderBase + ch * (SLIDER_H + SLIDER_GAP);
            if (my >= sy && my < sy + SLIDER_H) return ch;
        }
        return -1;
    }

    private void updateSlider(int mx, int ch) {
        int sx = x + 14;
        int sw = w - 50;
        int value = Math.max(0, Math.min(255, (int)((mx - sx) / (double) sw * 255)));

        int r = (tints[editingTint] >> 16) & 0xFF;
        int gv= (tints[editingTint] >>  8) & 0xFF;
        int b =  tints[editingTint]        & 0xFF;
        if (ch == 0) r = value;
        else if (ch == 1) gv = value;
        else b = value;

        tints[editingTint] = (r << 16) | (gv << 8) | b;
        hexInput = toHex(tints[editingTint]);
        pushToPartInfo();
    }

    private void applyHex() {
        try {
            int color = (int) Long.parseLong(hexInput, 16);
            tints[editingTint] = color & 0xFFFFFF;
            pushToPartInfo();
        } catch (NumberFormatException ignored) {}
    }

    private void pushToPartInfo() {
        PartInfo info = parent.getCurrentPartInfo();
        if (info != null && info.hasPart) {
            info.tints = tints.clone();
            info.needsTextureCompile = true;
            parent.refreshLivePreview();
        }
    }

    private static String toHex(int color) {
        return String.format("%06X", color & 0xFFFFFF);
    }

    private static boolean isHexChar(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }
}
