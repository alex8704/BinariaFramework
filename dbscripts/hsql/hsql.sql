CREATE CACHED TABLE recursos (
  id_recurso integer IDENTITY NOT NULL,
  recurso varchar(512) DEFAULT NULL
) ;

CREATE CACHED TABLE roles (
  id_rol integer IDENTITY NOT NULL,
  nombre_rol varchar(45) DEFAULT NULL,
  descripcion_rol varchar(256) DEFAULT NULL,
  CONSTRAINT nombre_rol_UNIQUE UNIQUE (nombre_rol)
) ;

CREATE CACHED TABLE recursos_x_roles (
  id_rol integer NOT NULL,
  id_recurso integer NOT NULL,
  PRIMARY KEY (id_rol,id_recurso),
  CONSTRAINT fk_resorcerole_resid FOREIGN KEY (id_recurso) REFERENCES recursos (id_recurso) ON UPDATE NO ACTION,
  CONSTRAINT fk_resorcerole_rolid FOREIGN KEY (id_rol) REFERENCES roles (id_rol) ON DELETE NO ACTION
) ;

CREATE CACHED TABLE usuarios (
  id_usuario integer IDENTITY NOT NULL,
  nombre_usuario varchar(45) DEFAULT NULL,
  password varchar(512) DEFAULT NULL,
  password_salt varchar(256) DEFAULT NULL,
  CONSTRAINT nombre_usuario_UNIQUE UNIQUE (nombre_usuario)
) ;

CREATE CACHED TABLE roles_x_usuarios (
  id_usuario integer NOT NULL,
  id_rol integer NOT NULL,
  PRIMARY KEY (id_usuario,id_rol),
  CONSTRAINT fk_userroles_rolid FOREIGN KEY (id_rol) REFERENCES roles (id_rol) ON UPDATE NO ACTION,
  CONSTRAINT fk_userroles_userid FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario) ON DELETE NO ACTION
) ;

CREATE CACHED TABLE gateways (
  id_gateway integer IDENTITY NOT NULL,
  ip_gateway varchar(45) DEFAULT NULL,
  descripcion varchar(100) DEFAULT NULL,
  feha_comunicacion timestamp DEFAULT NULL,
  id_creation_user integer NOT NULL,
  modification_date timestamp NOT NULL,
  CONSTRAINT fk_gateways_creat_userid FOREIGN KEY (id_creation_user) REFERENCES usuarios (id_usuario) ON DELETE NO ACTION
);


CREATE CACHED TABLE suscriptores (
  id_suscriptor integer IDENTITY NOT NULL,
  nombre varchar(100) DEFAULT NULL,
  identificacion varchar(15) DEFAULT NULL,
  tipo_identificacion varchar(5) DEFAULT NULL,
  fecha_nacimiento timestamp DEFAULT NULL,
  activo varchar(1) DEFAULT 'S' NOT NULL 
) ;

CREATE CACHED TABLE medidores (
  id_medidor integer IDENTITY NOT NULL,
  serial varchar(45) DEFAULT NULL,
  id_gateway integer DEFAULT NULL,
  id_suscriptor integer DEFAULT NULL,
  fecha_instalacion timestamp DEFAULT NULL,
  lectura_inicial decimal(10,2) DEFAULT NULL,
  CONSTRAINT fk_med_gateway FOREIGN KEY (id_gateway) REFERENCES gateways (id_gateway),
  CONSTRAINT fk_med_suscript FOREIGN KEY (id_suscriptor) REFERENCES suscriptores (id_suscriptor)
) ;

INSERT INTO usuarios VALUES (1,'admin123','ae454a608ba2bbcea43da001c640633a0ce14fc672de35b1c409f9a2164688f6','53d3eb556755fc2642d3659195c3b1c9');
