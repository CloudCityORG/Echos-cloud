-- 更新榜单热度 ZSet
-- KEYS[1]: ZSet key
-- ARGV[1]: rankId（成员）
-- ARGV[2]: 最终分数

local key = KEYS[1]
local member = ARGV[1]
local score = tonumber(ARGV[2])

-- 更新前排名（ZRANK 返回 0-based，不存在返回 false）
local oldRank = redis.call('ZRANK', key, member)
if oldRank == false then
    oldRank = -1
end

-- 添加/更新分数
redis.call('ZADD', key, score, member)

-- 更新后排名
local newRank = redis.call('ZRANK', key, member)
if newRank == false then
    newRank = -1
end

-- 当前榜首（分数最高）
local top = redis.call('ZREVRANGE', key, 0, 0)
local topMember = ""
if #top > 0 then
    topMember = top[1]
end

return {oldRank, newRank, topMember}
