package com.leeo.common.entity.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 系统样式类型
 *
 */
public enum SysThemesEnum {
	
	BOOTSTRAP_STYLE("default","admin/bootstrap/","default", "Bootstrap风格"),
	UIKIT_STYLE("shortcut","admin/uikit/","default", "UIKit风格"),
	ACE_STYLE("ace","admin/ace/","metro", "ACE平面风格");


    /**
     * 风格
     */
    private String style;
    
    /**
     * 首页路径
     */
    private String indexPath;
    
    /**
     * 样式
     */
    private String themes;
    /**
     * 描述
     */
    private String desc;

    private SysThemesEnum(String style, String indexPath, String themes, String desc) {
        this.style = style;
        this.indexPath = indexPath;
        this.themes = themes;
        this.desc = desc;
    }
    
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getThemes() {
		return themes;
	}

	public void setThemes(String themes) {
		this.themes = themes;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getIndexPath() {
		return indexPath;
	}

	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

	public static SysThemesEnum toEnum(String style) {
		if (StringUtils.isEmpty(style)) {
			//默认风格
			return BOOTSTRAP_STYLE;
        }
		for(SysThemesEnum item : SysThemesEnum.values()) {
			if(item.getStyle().equals(style)) {
				return item;
			}
		}
		//默认风格
		return BOOTSTRAP_STYLE;
	}

    public String toString() {
        return "{style: " + style + ", indexPath: " + indexPath + ", themes: " + themes + ", desc: " + desc +"}";
    }
}
