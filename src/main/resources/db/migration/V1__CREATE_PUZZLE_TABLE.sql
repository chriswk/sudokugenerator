CREATE TABLE IF NOT EXISTS puzzle(
    id BIGSERIAL PRIMARY KEY,
    ulid char(26),
    puzzle char(81) not null,
    puzzle_gen_ms BIGINT,
    solution char(81) not null,
    solution_gen_ms BIGINT,
    difficulty text not null,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT (now() at time zone 'utc')
);
CREATE UNIQUE INDEX puzzle_ulid_uniq_idx ON puzzle(ulid);
CREATE UNIQUE INDEX puzzle_solution_uniq_idx ON puzzle(puzzle, solution);
