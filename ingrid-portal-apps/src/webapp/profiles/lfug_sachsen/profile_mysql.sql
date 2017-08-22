-- Hide '/main-measures.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/main-measures.psml';

-- Hide '/main-chronicle.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/main-chronicle.psml';

-- Hide '/language.link'
UPDATE link SET is_hidden = 1 WHERE path = '/language.link';