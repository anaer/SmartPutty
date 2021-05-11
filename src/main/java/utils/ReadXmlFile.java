package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONUtil;
import constants.ConstantValue;
import lombok.extern.slf4j.Slf4j;

/**
 * 读取xml配置信息.
 *
 * @author lvcn
 * @version $Id: ReadXMLFile.java, v 1.0 Jul 22, 2019 3:46:18 PM lvcn Exp $
 */
@Slf4j
public class ReadXmlFile {

    /**
     * 读取xml配置文件.
     * @param filePath 文件路径
     */
    public static List<HashMap<String, String>> parse(String filePath) {
        List<HashMap<String, String>> list = null;
        if (FileUtil.isFile(filePath)) {
            list = parse(new File(filePath));
        } else {
            log.error("配置文件不存在. {}", filePath);
        }
        return list;
    }

    /**
     * 读取xml配置文件.
     *
     * @param file xml文件
     * @return 配置列表
     */
    public static List<HashMap<String, String>> parse(File file) {
        List<HashMap<String, String>> ret = new ArrayList<>();
        try {
            Document doc = XmlUtil.readXML(file);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("batch");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String type = eElement.getAttribute("type");
                    String path = getElementText(eElement, "path");
                    String argument = getElementText(eElement, "argument");
                    String description = getElementText(eElement, "description");

                    // 过滤配置了绝对路径, 但是路径不存在的配置
                    if (!(FileUtil.isAbsolutePath(path) && !FileUtil.exist(path))) {
                        HashMap<String, String> hm = new HashMap<>(4);
                        hm.put("type", type);
                        hm.put("path", path);
                        hm.put("argument", argument);
                        hm.put("description", description);
                        ret.add(hm);
                    }
                }
            }

        } catch (Exception e) {
            log.error("xml解析失败.", e);
        }

        return ret;
    }

    /**
     * 获取子标签内容.
     *
     * @param element
     * @param tagName
     * @return
     */
    private static String getElementText(Element element, String tagName) {
        String res = "";
        NodeList ele = element.getElementsByTagName(tagName);
        if (ele != null && ele.getLength() > 0) {
            res = ele.item(0).getTextContent();
        }

        return res;
    }

    public static void main(String argv[]) {
        String path = ConstantValue.CONFIG_BATCH_FILE;
        File file = new File(path);
        List<HashMap<String, String>> list = parse(file);
        log.info("{}", JSONUtil.toJsonStr(list));
    }

}