drop table if exists `sys_user`;
drop table if exists `sys_resource`;
drop table if exists `sys_permission`;
drop table if exists `sys_role`;
drop table if exists `sys_role_resource_permission`;
drop table if exists `sys_group`;
drop table if exists `sys_user_group`;
drop table if exists `sys_auth`;

create table `sys_user`(
  `id`         bigint not null auto_increment,
  `username`  varchar(100),
  `email`  varchar(100),
  `mobile_phone_number`  varchar(20),
  `password`  varchar(100),
  `salt`       varchar(10),
  `create_date` timestamp default 0,
  `status`    varchar(50),
  `deleted`   bool,
  `admin`     bool,
  `update_timestamp`  datetime NULL DEFAULT NULL ,
  `create_timestamp`  datetime NULL DEFAULT NULL ,
  constraint `pk_sys_user` primary key(`id`),
  constraint `unique_sys_user_username` unique(`username`),
  constraint `unique_sys_user_email` unique(`email`),
  constraint `unique_sys_user_mobile_phone_number` unique(`mobile_phone_number`),
  index `idx_sys_user_status` (`status`)
) charset=utf8 ENGINE=InnoDB;

create table `sys_resource`(
  `id`         bigint not null auto_increment,
  `name`      varchar(100),
  `identity`  varchar(100),
  `url`      varchar(200),
  `parent_id` bigint,
  `parent_ids`  varchar(200) default '',
  `icon`       varchar(200),
  `weight`    int,
  `is_show`       bool,
  `update_timestamp`  datetime NULL DEFAULT NULL ,
  `create_timestamp`  datetime NULL DEFAULT NULL ,
  constraint `pk_sys_resource` primary key(`id`),
  index `idx_sys_resource_name` (`name`),
  index `idx_sys_resource_identity` (`identity`),
  index `idx_sys_resource_user` (`url`),
  index `idx_sys_resource_parent_id` (`parent_id`),
  index `idx_sys_resource_parent_ids_weight` (`parent_ids`, `weight`)
) charset=utf8 ENGINE=InnoDB;

create table `sys_permission`(
  `id`         bigint not null auto_increment,
  `name`      varchar(100),
  `permission`  varchar(100),
  `description`      varchar(200),
  `is_show`       bool,
  `update_timestamp`  datetime NULL DEFAULT NULL ,
  `create_timestamp`  datetime NULL DEFAULT NULL ,
  constraint `pk_sys_permission` primary key(`id`),
  index idx_sys_permission_name (`name`),
  index idx_sys_permission_permission (`permission`),
  index idx_sys_permission_show (`is_show`)
) charset=utf8 ENGINE=InnoDB;

create table `sys_role`(
  `id`         bigint not null auto_increment,
  `name`      varchar(100),
  `role`  varchar(100),
  `description`      varchar(200),
  `is_show`       bool,
  `update_timestamp`  datetime NULL DEFAULT NULL ,
  `create_timestamp`  datetime NULL DEFAULT NULL ,
  constraint `pk_sys_role` primary key(`id`),
  index `idx_sys_role_name` (`name`),
  index `idx_sys_role_role` (`role`),
  index `idx_sys_role_show` (`is_show`)
) charset=utf8 ENGINE=InnoDB;

create table `sys_role_resource_permission`(
  `id`         bigint not null auto_increment,
  `role_id`   bigint,
  `resource_id` bigint,
  `permission_ids` varchar(500),
  `update_timestamp`  datetime NULL DEFAULT NULL ,
  `create_timestamp`  datetime NULL DEFAULT NULL ,
  constraint `pk_sys_role_resource_permission` primary key(`id`),
  constraint `unique_sys_role_resource_permission` unique(`role_id`, `resource_id`)
) charset=utf8 ENGINE=InnoDB;

create table `sys_group`(
  `id`         bigint not null auto_increment,
  `name`       varchar(100),
  `type`       varchar(50),
  `is_show`       bool,
  `update_timestamp`  datetime NULL DEFAULT NULL ,
  `create_timestamp`  datetime NULL DEFAULT NULL ,
  constraint `pk_sys_group` primary key(`id`),
  index `idx_sys_group_type` (`type`),
  index `idx_sys_group_show` (`is_show`)
) charset=utf8 ENGINE=InnoDB;

create table `sys_user_group`(
  `id`         bigint not null auto_increment,
  `user_id`        bigint,
  `group_id`       bigint,
  `update_timestamp`  datetime NULL DEFAULT NULL ,
  `create_timestamp`  datetime NULL DEFAULT NULL ,
  constraint `pk_sys_user_group` primary key(`id`),
  index `idx_sys_user_group_user` (`user_id`),
  index `idx_sys_user_group_group` (`group_id`)
) charset=utf8 ENGINE=InnoDB;

create table `sys_auth`(
  `id`         bigint not null auto_increment,
  `user_id`        bigint,
  `group_id`       bigint,
  `role_ids`       varchar(500),
  `type`           varchar(50),
  constraint `pk_sys_auth` primary key(`id`),
  index `idx_sys_auth_user` (`user_id`),
  index `idx_sys_auth_group` (`group_id`),
  index `idx_sys_auth_type` (`type`)
) charset=utf8 ENGINE=InnoDB;

-- 权限
insert into `sys_permission` values (1, '所有', 'all', '所有数据操作的权限', 1, NOW(), NOW());
insert into `sys_permission` values (2, '新增', 'save', '新增数据操作的权限', 1, NOW(), NOW());
insert into `sys_permission` values (3,  '修改', 'update', '修改数据操作的权限', 1, NOW(), NOW());
insert into `sys_permission` values (4,  '删除', 'delete', '删除数据操作的权限', 1, NOW(), NOW());
insert into `sys_permission` values (5,  '查看', 'view', '查看数据操作的权限', 1, NOW(), NOW());
insert into `sys_permission` values (6,  '不显示的权限', 'none', '不显示的权限', 0, NOW(), NOW());

-- 角色
insert into `sys_role` values (1,  '管理员', 'admin', '拥有所有权限', 1, NOW(), NOW());
insert into `sys_role` values (2,  '测试人员', 'test', '测试人员', 1, NOW(), NOW());
insert into `sys_role` values (3,  '不显示的角色', 'none', '测试人员', 0, NOW(), NOW());

-- 资源
insert into `sys_resource`(`id`, `parent_id`, `parent_ids`, weight, `name`, `identity`, `url`, `is_show`, `update_timestamp`, `create_timestamp`)
 values (1, 0, '0/', 1, '示例列表', 'example:example', '/showcase/sample', true, NOW(), NOW());
insert into `sys_resource`(`id`, `parent_id`, `parent_ids`, weight, `name`, `identity`, `url`, `is_show`, `update_timestamp`, `create_timestamp`)
 values (2, 0, '0/', 2, '逻辑删除列表', 'example:deleted', '/showcase/deleted', false, NOW(), NOW());
insert into `sys_resource`(`id`, `parent_id`, `parent_ids`, weight, `name`, `identity`, `url`, `is_show`, `update_timestamp`, `create_timestamp`)
 values (3, 0, '0/', 4, '文件上传列表', 'example:upload', '/showcase/upload', true, NOW(), NOW());

-- 角色--资源--权限
insert into `sys_role_resource_permission` (`id`, `role_id`, `resource_id`, `permission_ids`, `update_timestamp`, `create_timestamp`)
    values(1, 1, 1, '1,2,6', NOW(), NOW());
insert into `sys_role_resource_permission` (`id`, `role_id`, `resource_id`, `permission_ids`, `update_timestamp`, `create_timestamp`)
    values(2, 1, 2, '1,3,5', NOW(), NOW());
insert into `sys_role_resource_permission` (`id`, `role_id`, `resource_id`, `permission_ids`, `update_timestamp`, `create_timestamp`)
    values(3, 2, 3, '1,3,6', NOW(), NOW());
insert into `sys_role_resource_permission` (`id`, `role_id`, `resource_id`, `permission_ids`, `update_timestamp`, `create_timestamp`)
    values(4, 3, 1, '1,4,6', NOW(), NOW());
    
-- 分组
insert into `sys_group` (`id`, `name`, `type`, `is_show`, `update_timestamp`, `create_timestamp`)
   values(1, '管理员', 'admin', true, NOW(), NOW());
insert into `sys_group` (`id`, `name`, `type`, `is_show`, `update_timestamp`, `create_timestamp`)
   values(2, '用户组', 'user', true, NOW(), NOW());
   
-- 用户--分组
insert into `sys_user_group` (`id`, `group_id`, `user_id`, `update_timestamp`, `create_timestamp`)
    values(1, 2, 1, NOW(), NOW());
    
-- 用户组授权
insert into sys_auth (`id`, `user_id`, `group_id`, `role_ids`, `type`)
    values(1, 0, 1, '1,3', 'user_group');
insert into sys_auth (`id`, `user_id`, `group_id`, `role_ids`, `type`)
    values(2, 0, 2, '2,3', 'user_group');