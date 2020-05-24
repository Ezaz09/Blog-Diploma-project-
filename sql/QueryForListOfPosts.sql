DROP TABLE IF EXISTS postsWithInf;

CREATE TEMPORARY TABLE postsWithInf(
SELECT
	p.id as "id",
    p.title AS 'title',
    sum(CASE
        WHEN p_votes.value = 1 THEN p_votes.value
    END) AS 'likeCount',
    sum(CASE
        WHEN p_votes.value = - 1 THEN p_votes.value
    END) AS 'dislikeCount',
    p.view_count AS 'viewCount'
FROM
    posts AS p
        LEFT JOIN
    posts_votes AS p_votes ON p.id = p_votes.post_id
WHERE
    is_active = 1
        AND moderation_status = 'ACCEPTED');
        
SELECT 
    title,
    likeCount,
    dislikeCount,
    viewCount,
    COUNT(p_comments.id) AS "commentCount"
FROM
    postsWithInf AS p
        LEFT JOIN
    post_comments AS p_comments ON p.id = p_comments.post_id;