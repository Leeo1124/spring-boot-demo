package com.leeo.sys.resource.entity;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class Menu implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
    private String name;
    private String icon;
    private String url;
    private Long parentId;
    // 父节点
 	private Menu parent = null;

    private List<Menu> children = Lists.newArrayList();

    public Menu(Long id, String name, String icon, String url, Long parentId) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.url = url;
        this.parentId = parentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }
    
    public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}
	
	public void addChildren(Menu menu) {
    	menu.setParent(this); // 设置父节点
		this.children.add(menu);
	}

	/**
     * @return
     */
    public boolean isHasChildren() {
        return !getChildren().isEmpty();
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", url='" + url + '\'' +
                ", children=" + children +
                '}';
    }
}
