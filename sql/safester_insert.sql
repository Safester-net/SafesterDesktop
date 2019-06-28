INSERT INTO user_number_increment VALUES (3);

INSERT INTO user_login  VALUES (1, 'nico@safelogic.com',        'nico@safelogic.com',       '6210274434052f20fcb8');
INSERT INTO user_login  VALUES (2, 'alex@safelogic.com',        'alex@safelogic.com',       '6210274434052f20fcb8');
INSERT INTO user_login  VALUES (3, 'guillaume@safelogic.com',   'guillaume@safelogic.com',  '6210274434052f20fcb8');

INSERT INTO user_settings VALUES (1, 'Nicolas de Pomereu',      'nico@safelogic.com',  false, false);
INSERT INTO user_settings VALUES (2, 'Alexandre Becquereau',    'alex@safelogic.com',  false, false);
INSERT INTO user_settings VALUES (3, 'guillaume@safelogic.com', 'guillaume@safelogic.com', false, false);

INSERT INTO folder VALUES ( 1, 1, 'inbox');
INSERT INTO folder VALUES ( 1, 2, 'outbox');
INSERT INTO folder VALUES ( 1, 3, 'trash');

INSERT INTO folder VALUES ( 2, 1, 'inbox');
INSERT INTO folder VALUES ( 2, 2, 'outbox');
INSERT INTO folder VALUES ( 2, 3, 'trash');

INSERT INTO folder VALUES ( 3, 1, 'inbox');
INSERT INTO folder VALUES ( 3, 2, 'outbox');
INSERT INTO folder VALUES ( 3, 3, 'trash');

INSERT INTO folder VALUES ( 1, 4, 'my_folder_nico');
INSERT INTO folder VALUES ( 2, 4, 'my_folder_alex');
INSERT INTO folder VALUES ( 3, 4, 'my_folder_guillaume');

INSERT INTO child_folder VALUES ( 1, 1, 4);
INSERT INTO child_folder VALUES ( 2, 1, 4);
INSERT INTO child_folder VALUES ( 3, 1, 4);

INSERT INTO address_book VALUES(1, 1, 'Nicolas de Pomereu', 'nico@safelogic.com');
INSERT INTO address_book VALUES(1, 2, 'Alexandre Becquereau', 'alex@safelogic.com');
INSERT INTO address_book VALUES(1, 3, 'Guillaume Rigal', 'guillaume@safelogic.com');

INSERT INTO address_book VALUES(2, 1, 'Nicolas de Pomereu', 'nico@safelogic.com');
INSERT INTO address_book VALUES(2, 2, 'Alexandre Becquereau', 'alex@safelogic.com');
INSERT INTO address_book VALUES(2, 3, 'Guillaume Rigal', 'guillaume@safelogic.com');
