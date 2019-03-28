CREATE TABLE defs (
    defname text,
    deforder timeuuid,
    pid text,
    value text,
    PRIMARY KEY (defname, deforder, pid)
) WITH CLUSTERING ORDER BY (deforder DESC, pid ASC);


INSERT INTO defs (defname, deforder, pid, value)
VALUES ('aaa', now(), 'pid', 'v1');

INSERT INTO defs (defname, deforder, pid, value)
VALUES ('aaa', now(), 'pid', 'v2');

INSERT INTO defs (defname, deforder, pid, value)
VALUES ('aaa', now(), 'pid', 'v3');


INSERT INTO defs (defname, deforder, pid, value)
VALUES ('bbb', now(), 'pid', 'v1b');

INSERT INTO defs (defname, deforder, pid, value)
VALUES ('bbb', now(), 'pid', 'v2b');

INSERT INTO defs (defname, deforder, pid, value)
VALUES ('bbb', now(), 'pid', 'v3b');