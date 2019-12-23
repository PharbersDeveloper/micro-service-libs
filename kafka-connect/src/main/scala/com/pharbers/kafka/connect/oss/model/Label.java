package com.pharbers.kafka.connect.oss.model;

import com.pharbers.kafka.schema.OssTask;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/11/12 13:51
 */
public class Label {
    private List<String> label;
    private List<String> dataCover;
    private List<String> geoCover;
    private List<String> markets;
    private List<String> molecules;
    private List<String> providers;
    private String sheetName;
    private String fileName;

    private String assetId;

    public Label(List<String> label,
                 List<String> dataCover,
                 List<String> geoCover,
                 List<String> markets,
                 List<String> molecules,
                 List<String> providers,
                 String sheetName,
                 String fileName,
                 String assetId) {
        this.label = label;
        this.dataCover = dataCover;
        this.geoCover = geoCover;
        this.markets = markets;
        this.molecules = molecules;
        this.providers = providers;
        this.sheetName = sheetName;
        this.fileName = fileName;
        this.assetId = assetId;
    }

    public Label(OssTask task, String sheetName) {
        this.label = new ArrayList<>();
        task.getLabels().forEach(x -> label.add(x.toString()));
        this.dataCover = new ArrayList<>();
        task.getDataCover().forEach(x -> dataCover.add(x.toString()));
        this.geoCover = new ArrayList<>();
        task.getGeoCover().forEach(x -> geoCover.add(x.toString()));
        this.markets = new ArrayList<>();
        task.getMarkets().forEach(x -> markets.add(x.toString()));
        this.molecules = new ArrayList<>();
        task.getMolecules().forEach(x -> molecules.add(x.toString()));
        this.providers = new ArrayList<>();
        task.getProviders().forEach(x -> providers.add(x.toString()));
        this.sheetName = sheetName;
        this.fileName = task.getFileName().toString();
        this.assetId = task.getAssetId().toString();
    }

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public List<String> getDataCover() {
        return dataCover;
    }

    public void setDataCover(List<String> dataCover) {
        this.dataCover = dataCover;
    }

    public List<String> getGeoCover() {
        return geoCover;
    }

    public void setGeoCover(List<String> geoCover) {
        this.geoCover = geoCover;
    }

    public List<String> getMarkets() {
        return markets;
    }

    public void setMarkets(List<String> markets) {
        this.markets = markets;
    }

    public List<String> getMolecules() {
        return molecules;
    }

    public void setMolecules(List<String> molecules) {
        this.molecules = molecules;
    }

    public List<String> getProviders() {
        return providers;
    }

    public void setProviders(List<String> providers) {
        this.providers = providers;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
}
