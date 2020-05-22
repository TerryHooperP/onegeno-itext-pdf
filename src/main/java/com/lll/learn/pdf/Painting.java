package com.lll.learn.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import lombok.Data;

import java.io.IOException;

/**
 * @author: laoliangliang
 * @description:
 * @create: 2020/5/20 8:34
 **/
@Data
public class Painting {
    private PdfCanvas pdfCanvas;
    private PageSize pageSize;
    private PdfDocument pdf;
    private ReportBuilder builder;

    public Painting(PdfDocument pdf) {
        this.pdf = pdf;
        PdfPage page = pdf.getPage(pdf.getNumberOfPages());
        pageSize = pdf.getDefaultPageSize();
        pdfCanvas = new PdfCanvas(page);
        pdfCanvas.concatMatrix(1, 0, 0, 1, 0, pageSize.getHeight());
    }

    public Painting(PdfDocument pdf, ReportBuilder builder) {
        this(pdf);
        this.builder = builder;
    }

    public Painting(PdfDocument pdf, PdfCanvas pdfCanvas,ReportBuilder builder) {
        this.pdf = pdf;
        pageSize = pdf.getDefaultPageSize();
        this.pdfCanvas = pdfCanvas;
        this.pdfCanvas.concatMatrix(1, 0, 0, 1, 0, pageSize.getHeight());
        this.builder = builder;
    }

    public void drawHeader() {
        pdfCanvas.setStrokeColor(GenoColor.getThemeColor());
        pdfCanvas.setLineWidth(0.7f);
        // 画头线（在开头画会使整个页面画不出来）
        int y = -40;
        pdfCanvas.moveTo(60, y)
                .lineTo(pageSize.getWidth() / 2 - 60, y).stroke();
        pdfCanvas.moveTo(pageSize.getWidth() / 2 + 60, y)
                .lineTo(pageSize.getWidth() - 60, y).stroke();
    }

    public void drawSegment(float score) {
        Color color = null;
        pdfCanvas.setStrokeColor(ColorConstants.BLACK);
        pdfCanvas.setLineWidth(1);
        String[] segmenets = Constant.SEGMENETS;
        if (score >= Float.parseFloat(segmenets[0]) && score < Float.parseFloat(segmenets[2])) {
            color = GenoColor.getLightBlueColor();
        } else if (score >= Float.parseFloat(segmenets[2]) && score < Float.parseFloat(segmenets[3])) {
            color = GenoColor.getThemeColor();
        } else if (score >= Float.parseFloat(segmenets[3]) && score < Float.parseFloat(segmenets[4])) {
            color = GenoColor.getOrangeColor();
        } else if (score >= Float.parseFloat(segmenets[4]) && score <= Float.parseFloat(segmenets[5])) {
            color = GenoColor.getRedColor();
        }
        // 位置标示偏移量
        float posXOffset = -100;
        // 画线段
        for (int i = 0; i < 6; i++) {
            pdfCanvas.saveState().moveTo(pageSize.getWidth() / 2 - 100 + i * 40, -203)
                    .lineTo(pageSize.getWidth() / 2 - 100 + i * 40, -208)
                    .stroke().restoreState();
            try {
                int left = 103;
                if (i > 0) {
                    left = 107;
                }
                pdfCanvas.beginText()
                        .setFontAndSize(PdfFontFactory.createFont(StandardFonts.SYMBOL), 12)
                        .moveText(pageSize.getWidth() / 2 - left + i * 40, -218);
                pdfCanvas.newlineShowText(segmenets[i]);
                pdfCanvas.endText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i < segmenets.length - 1) {
                float v1 = Float.parseFloat(segmenets[i]);
                float v2 = Float.parseFloat(segmenets[i + 1]);
                if (score >= v1 && score < v2) {
                    posXOffset = posXOffset + 40 / (v2 - v1) * (score - v1) + 40 * i;
                }
            }
        }
        // 画三角形
        pdfCanvas.setFillColor(color);
        pdfCanvas.moveTo(pageSize.getWidth() / 2 + posXOffset, -193)
                .lineTo(pageSize.getWidth() / 2 - 3.4 + posXOffset, -187)
                .lineTo(pageSize.getWidth() / 2 + 3.4 + posXOffset, -187)
                .fill();
        // 画圆
        pdfCanvas.setLineWidth(2);
        pdfCanvas.setStrokeColor(color);
        pdfCanvas.saveState()
                .roundRectangle(pageSize.getWidth() / 2 - 3 + posXOffset, -188, 6, 6, 3)
                .stroke()
                .restoreState();
        // 画进度条
        pdfCanvas.setStrokeColor(color);
        pdfCanvas.saveState()
                .roundRectangle(pageSize.getWidth() / 2 - 100, -200, 200, 5, 3)
                .stroke()
                .restoreState();
        pdfCanvas.saveState()
                .roundRectangle(pageSize.getWidth() / 2 - 100, -200, posXOffset + 100, 5, 3)
                .fill()
                .restoreState();
    }

    public void close() {
        pdfCanvas.closePath();
        pdfCanvas.closePathFillStroke();
        pdfCanvas.release();
    }

    public void drawHeaderText(String text) {
        pdfCanvas.beginText()
                .setFontAndSize(builder.font, 12)
                .moveText(pageSize.getWidth() / 2 - text.length() * 12 / 2, -45);
        pdfCanvas.showText(text);
        pdfCanvas.endText();
    }
}