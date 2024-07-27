-- liquibase formatted sql

-- changeset John:1722082932749-1
CREATE TABLE distributed_execution_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, extra_execute_content LONGTEXT NOT NULL, has_error BIT(1) NOT NULL, is_done BIT(1) NOT NULL, is_last_of_extra_execute_content BIT(1) NOT NULL, name VARCHAR(255) NOT NULL, page_num BIGINT NOT NULL, page_size BIGINT NOT NULL, pagination LONGTEXT NOT NULL, total_page BIGINT NOT NULL, total_record BIGINT NOT NULL, unique_code_of_extra_execute_content VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, version VARCHAR(255) NOT NULL, CONSTRAINT PK_DISTRIBUTED_EXECUTION_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-2
CREATE TABLE encrypt_decrypt_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, private_key_ofrsa LONGTEXT NOT NULL, public_key_ofrsa LONGTEXT NOT NULL, secret_key_ofaes LONGTEXT NOT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_ENCRYPT_DECRYPT_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-3
CREATE TABLE friendship_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, has_initiative BIT(1) NOT NULL, is_friend BIT(1) NOT NULL, is_in_blacklist BIT(1) NOT NULL, secret_key_ofaes LONGTEXT NOT NULL, update_date datetime(6) NOT NULL, friend_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_FRIENDSHIP_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-4
CREATE TABLE logger_entity (id VARCHAR(255) NOT NULL, caller_class_name LONGTEXT NOT NULL, caller_line_number BIGINT NOT NULL, caller_method_name VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, exception_class_name LONGTEXT NOT NULL, exception_message LONGTEXT NOT NULL, exception_stack_trace LONGTEXT NOT NULL, git_commit_date datetime(6) NOT NULL, git_commit_id VARCHAR(255) NOT NULL, has_exception BIT(1) NOT NULL, is_active BIT(1) NOT NULL, level VARCHAR(255) NOT NULL, logger_name LONGTEXT NOT NULL, message LONGTEXT NOT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_LOGGER_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-5
CREATE TABLE long_term_task_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, is_done BIT(1) NOT NULL, result LONGTEXT NOT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_LONG_TERM_TASK_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-6
CREATE TABLE organize_closure_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, ancestor_id VARCHAR(255) NOT NULL, descendant_id VARCHAR(255) NOT NULL, CONSTRAINT PK_ORGANIZE_CLOSURE_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-7
CREATE TABLE organize_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, deactive_key VARCHAR(255) NOT NULL, is_active BIT(1) NOT NULL, name VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, parent_id VARCHAR(255) NULL, CONSTRAINT PK_ORGANIZE_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-8
CREATE TABLE organize_move_top_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NULL, CONSTRAINT PK_ORGANIZE_MOVE_TOP_ENTITY PRIMARY KEY (id), UNIQUE (organize_id));

-- changeset John:1722082932749-9
CREATE TABLE storage_space_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, folder_name VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_STORAGE_SPACE_ENTITY PRIMARY KEY (id), UNIQUE (folder_name));

-- changeset John:1722082932749-10
CREATE TABLE system_default_role_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, name VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_SYSTEM_DEFAULT_ROLE_ENTITY PRIMARY KEY (id), UNIQUE (name));

-- changeset John:1722082932749-11
CREATE TABLE system_role_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, deactive_key VARCHAR(255) NOT NULL, is_active BIT(1) NOT NULL, name VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NULL, CONSTRAINT PK_SYSTEM_ROLE_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-12
CREATE TABLE system_role_relation_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, system_default_role_id VARCHAR(255) NOT NULL, system_role_id VARCHAR(255) NOT NULL, CONSTRAINT PK_SYSTEM_ROLE_RELATION_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-13
CREATE TABLE token_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, is_active BIT(1) NOT NULL, unique_one_time_password_logo VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_TOKEN_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-14
CREATE TABLE user_black_organize_closure_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_BLACK_ORGANIZE_CLOSURE_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-15
CREATE TABLE user_black_organize_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_BLACK_ORGANIZE_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-16
CREATE TABLE user_email_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, deactive_key VARCHAR(255) NOT NULL, email VARCHAR(512) NOT NULL, is_active BIT(1) NOT NULL, update_date datetime(6) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_EMAIL_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-17
CREATE TABLE user_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, is_active BIT(1) NOT NULL, password LONGTEXT NOT NULL, private_key_ofrsa LONGTEXT NOT NULL, public_key_ofrsa LONGTEXT NOT NULL, update_date datetime(6) NOT NULL, username LONGTEXT NOT NULL, CONSTRAINT PK_USER_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-18
CREATE TABLE user_message_deactivate_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, user_id VARCHAR(255) NOT NULL, user_message_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_MESSAGE_DEACTIVATE_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-19
CREATE TABLE user_message_entity (id VARCHAR(255) NOT NULL, content LONGTEXT NOT NULL, create_date datetime(6) NOT NULL, file_name LONGTEXT NOT NULL, folder_name VARCHAR(255) NOT NULL, folder_size BIGINT NOT NULL, is_recall BIT(1) NOT NULL, update_date datetime(6) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_MESSAGE_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-20
CREATE TABLE user_role_relation_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, system_role_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_ROLE_RELATION_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-21
CREATE TABLE verification_code_email_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, email VARCHAR(512) NOT NULL, has_used BIT(1) NOT NULL, is_passed BIT(1) NOT NULL, update_date datetime(6) NOT NULL, verification_code VARCHAR(255) NOT NULL, CONSTRAINT PK_VERIFICATION_CODE_EMAIL_ENTITY PRIMARY KEY (id));

-- changeset John:1722082932749-22
ALTER TABLE organize_entity ADD CONSTRAINT UK1mhgsj2mhg2lhsa61a07lvfj0 UNIQUE (parent_id, name, deactive_key);

-- changeset John:1722082932749-23
ALTER TABLE user_role_relation_entity ADD CONSTRAINT UK2r7kn4ecomsubdkw64arb4myu UNIQUE (user_id, system_role_id);

-- changeset John:1722082932749-24
ALTER TABLE token_entity ADD CONSTRAINT UK5c8cpmrkalc5g4mty2t75lbdx UNIQUE (user_id, unique_one_time_password_logo);

-- changeset John:1722082932749-25
ALTER TABLE system_role_entity ADD CONSTRAINT UKg073i0k6v6paiqt8r64hiaht0 UNIQUE (organize_id, name, deactive_key);

-- changeset John:1722082932749-26
ALTER TABLE user_message_deactivate_entity ADD CONSTRAINT UKgor4r6qme9rknaoyfrqmvweh3 UNIQUE (user_message_id, user_id);

-- changeset John:1722082932749-27
ALTER TABLE user_email_entity ADD CONSTRAINT UKkc2p7w4m6bob6x3162493k2il UNIQUE (email, deactive_key);

-- changeset John:1722082932749-28
ALTER TABLE system_role_relation_entity ADD CONSTRAINT UKml3rfsnvl4rmqj4uspx7rl0uu UNIQUE (system_role_id, system_default_role_id);

-- changeset John:1722082932749-29
ALTER TABLE distributed_execution_entity ADD CONSTRAINT UKq7j5ona88grpqooi970x40ehc UNIQUE (version, page_num, page_size, unique_code_of_extra_execute_content);

-- changeset John:1722082932749-30
ALTER TABLE friendship_entity ADD CONSTRAINT UKqsamcn1vehewm87nlwtjw2kfk UNIQUE (user_id, friend_id);

-- changeset John:1722082932749-31
CREATE INDEX FK132burgawm3e386ygcj8co5vu ON user_message_deactivate_entity(user_id);

-- changeset John:1722082932749-32
CREATE INDEX FK1grbbc8n57ni2yg31n9rqvg3 ON system_role_relation_entity(system_default_role_id);

-- changeset John:1722082932749-33
CREATE INDEX FK23c715x0gcdv29x9l92r8dc74 ON user_message_entity(user_id);

-- changeset John:1722082932749-34
CREATE INDEX FK8p59ikpmch4vw781yhmj12nyv ON user_role_relation_entity(system_role_id);

-- changeset John:1722082932749-35
CREATE INDEX FKay1r2ryhqdkvs5ll7ltr1hthu ON user_black_organize_entity(organize_id);

-- changeset John:1722082932749-36
CREATE INDEX FKfrbs0stjmmje4we9n1t0cf0oh ON organize_closure_entity(ancestor_id);

-- changeset John:1722082932749-37
CREATE INDEX FKgeq95vaaswvbtq6g2evd4csp0 ON user_black_organize_closure_entity(organize_id);

-- changeset John:1722082932749-38
CREATE INDEX FKhkqth37t013xa04vx2myptqtk ON user_black_organize_entity(user_id);

-- changeset John:1722082932749-39
CREATE INDEX FKlac7iem33ph9eneiyxld7v6q8 ON user_black_organize_closure_entity(user_id);

-- changeset John:1722082932749-40
CREATE INDEX FKpq2at14h3gljwg848p43aw14w ON friendship_entity(friend_id);

-- changeset John:1722082932749-41
CREATE INDEX FKq1njl6uveplkpgu70115gbf5o ON user_email_entity(user_id);

-- changeset John:1722082932749-42
CREATE INDEX FKrq3vl8q12mdppvdre3bcv6fce ON organize_closure_entity(descendant_id);

-- changeset John:1722082932749-43
CREATE INDEX IDXg4isidkyw8pui6rnpe5ii9fdb ON user_message_entity(folder_name);

-- changeset John:1722082932749-44
CREATE INDEX IDXodg8kq20ypvfvf3is4473alec ON organize_entity(parent_id, is_active);

-- changeset John:1722082932749-45
CREATE INDEX IDXoxeihgs43v794u9lhrh79ugm5 ON verification_code_email_entity(email, create_date);

-- changeset John:1722082932749-46
ALTER TABLE user_message_deactivate_entity ADD CONSTRAINT FK132burgawm3e386ygcj8co5vu FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-47
ALTER TABLE system_role_relation_entity ADD CONSTRAINT FK1grbbc8n57ni2yg31n9rqvg3 FOREIGN KEY (system_default_role_id) REFERENCES system_default_role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-48
ALTER TABLE user_message_entity ADD CONSTRAINT FK23c715x0gcdv29x9l92r8dc74 FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-49
ALTER TABLE user_role_relation_entity ADD CONSTRAINT FK2gjhgy73kqr9f9xgvvp4uesm4 FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-50
ALTER TABLE friendship_entity ADD CONSTRAINT FK4n7gua4wuvh9ymsen9pdt49v6 FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-51
ALTER TABLE user_role_relation_entity ADD CONSTRAINT FK8p59ikpmch4vw781yhmj12nyv FOREIGN KEY (system_role_id) REFERENCES system_role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-52
ALTER TABLE user_black_organize_entity ADD CONSTRAINT FKay1r2ryhqdkvs5ll7ltr1hthu FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-53
ALTER TABLE organize_move_top_entity ADD CONSTRAINT FKbeaf63wml34x4fpcdftpi3efr FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-54
ALTER TABLE token_entity ADD CONSTRAINT FKchycpasyr16kt66k09e6ompve FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-55
ALTER TABLE organize_closure_entity ADD CONSTRAINT FKfrbs0stjmmje4we9n1t0cf0oh FOREIGN KEY (ancestor_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-56
ALTER TABLE organize_entity ADD CONSTRAINT FKg1i0kxqrixd8fdpw6me7x5t3q FOREIGN KEY (parent_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-57
ALTER TABLE user_black_organize_closure_entity ADD CONSTRAINT FKgeq95vaaswvbtq6g2evd4csp0 FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-58
ALTER TABLE user_black_organize_entity ADD CONSTRAINT FKhkqth37t013xa04vx2myptqtk FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-59
ALTER TABLE user_message_deactivate_entity ADD CONSTRAINT FKi41417apwfestnbkgv32u9amw FOREIGN KEY (user_message_id) REFERENCES user_message_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-60
ALTER TABLE user_black_organize_closure_entity ADD CONSTRAINT FKlac7iem33ph9eneiyxld7v6q8 FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-61
ALTER TABLE system_role_entity ADD CONSTRAINT FKmiiv7gynd0e029d9ceriilfwy FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-62
ALTER TABLE friendship_entity ADD CONSTRAINT FKpq2at14h3gljwg848p43aw14w FOREIGN KEY (friend_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-63
ALTER TABLE user_email_entity ADD CONSTRAINT FKq1njl6uveplkpgu70115gbf5o FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-64
ALTER TABLE organize_closure_entity ADD CONSTRAINT FKrq3vl8q12mdppvdre3bcv6fce FOREIGN KEY (descendant_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722082932749-65
ALTER TABLE system_role_relation_entity ADD CONSTRAINT FKt8w58hfw3wcq1k7ju07bgjvpo FOREIGN KEY (system_role_id) REFERENCES system_role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

