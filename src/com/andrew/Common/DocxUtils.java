package com.andrew.Common;

import org.docx4j.Docx4J;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.*;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.time.LocalDateTime;

public class DocxUtils {

    public static String buildReportReturnFile(String[] titles,String[] captions,String[] imgcodes){
        LocalDateTime localDateTime=LocalDateTime.now();
        String docxFileName=localDateTime.toLocalDate().toString()+"-"+localDateTime.toLocalTime().toString().replace(":",".")+".docx";
        File docxFile=new File(docxFileName);

        return docxFile.getAbsolutePath();
    }

    public static String buildReport(String path,String[] titles,String[] captions,String[] imgcodes){
        LocalDateTime localDateTime=LocalDateTime.now();
        String docxFileName=localDateTime.toLocalDate().toString()+"-"+localDateTime.toLocalTime().toString().replace(":",".")+".docx";
        File docxFile=new File(path+"/"+docxFileName);
        try {
            WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.createPackage();
            wordprocessingMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title", "数据库性能容量报告");
            for(int i=0;i<titles.length;i++){
                wordprocessingMLPackage.getMainDocumentPart().addStyledParagraphOfText("NoteHeading",titles[i]);
                wordprocessingMLPackage.getMainDocumentPart().addStyledParagraphOfText("CommentText",captions[i]);
                String base64Info=imgcodes[i];
                BASE64Decoder decoder=new BASE64Decoder();
                base64Info = base64Info.replaceAll(" ", "+");
                String[] arr = base64Info.split("base64,");
                byte[] buffer;
                try {
                    buffer = decoder.decodeBuffer(arr[1]);
                } catch (IOException e) {
                    throw new RuntimeException();
                }
                addImageToPackage(wordprocessingMLPackage, buffer);
            }
            Docx4J.save(wordprocessingMLPackage,docxFile);
        }catch (Exception dException){
            return null;
        }
        return docxFileName;
    }

    /**
     *  Docx4j拥有一个由字节数组创建图片部件的工具方法, 随后将其添加到给定的包中. 为了能将图片添加
     *  到一个段落中, 我们需要将图片转换成内联对象. 这也有一个方法, 方法需要文件名提示, 替换文本,
     *  两个id标识符和一个是嵌入还是链接到的指示作为参数.
     *  一个id用于文档中绘图对象不可见的属性, 另一个id用于图片本身不可见的绘制属性. 最后我们将内联
     *  对象添加到段落中并将段落添加到包的主文档部件.
     *
     *  @param wordMLPackage 要添加图片的包
     *  @param bytes         图片对应的字节数组
     *  @throws Exception    不幸的createImageInline方法抛出一个异常(没有更多具体的异常类型)
     */
    private static void addImageToPackage(WordprocessingMLPackage wordMLPackage,
                                          byte[] bytes) throws Exception {
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

        int docPrId = 1;
        int cNvPrId = 2;
        Inline inline = imagePart.createImageInline("Filename hint","Alternative text", docPrId, cNvPrId, false);

        P paragraph = addInlineImageToParagraph(inline);

        wordMLPackage.getMainDocumentPart().addObject(paragraph);
    }

    /**
     *  创建一个对象工厂并用它创建一个段落和一个可运行块R.
     *  然后将可运行块添加到段落中. 接下来创建一个图画并将其添加到可运行块R中. 最后我们将内联
     *  对象添加到图画中并返回段落对象.
     *
     * @param   inline 包含图片的内联对象.
     * @return  包含图片的段落
     */
    private static P addInlineImageToParagraph(Inline inline) {
        // 添加内联对象到一个段落中
        ObjectFactory factory = new ObjectFactory();
        P paragraph = factory.createP();
        R run = factory.createR();
        paragraph.getContent().add(run);
        Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        return paragraph;
    }
}
