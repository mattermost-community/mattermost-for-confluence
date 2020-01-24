package com.mattermost.serializer;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.DeviceTypeAwareRenderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.content.Edited;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.json.json.JsonArray;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.json.json.JsonString;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.opensymphony.util.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * This class tries to figure out all the relevant information from an event.
 */
public final class EventRenderer {
    private static final Map<Class<? extends ContentEvent>, String> EVENT_MAP;

    static {
        EVENT_MAP = new HashMap<>();
        EVENT_MAP.put(BlogPostCreateEvent.class, "blog_created");
        EVENT_MAP.put(BlogPostRemoveEvent.class, "blog_removed");
        EVENT_MAP.put(BlogPostRestoreEvent.class, "blog_restored");
        EVENT_MAP.put(BlogPostTrashedEvent.class, "blog_trashed");
        EVENT_MAP.put(BlogPostUpdateEvent.class, "blog_updated");
        EVENT_MAP.put(CommentRemoveEvent.class, "comment_removed");
        EVENT_MAP.put(CommentCreateEvent.class, "comment_created");
        EVENT_MAP.put(CommentUpdateEvent.class, "comment_updated");
        EVENT_MAP.put(PageCreateEvent.class, "page_created");
        EVENT_MAP.put(PageRemoveEvent.class, "page_removed");
        EVENT_MAP.put(PageRestoreEvent.class, "page_restored");
        EVENT_MAP.put(PageTrashedEvent.class, "page_trashed");
        EVENT_MAP.put(PageUpdateEvent.class, "page_updated");
    }

    private EventRenderer() {
    }

    public static JsonObject renderEvent(final ContentEvent event) {
        if (EVENT_MAP.get(event.getClass()) == null) {
            return null;
        }

        JsonObject result = new JsonObject();
        result.setProperty("object", renderContentJson(event.getContent()));
        result.setProperty("event", EVENT_MAP.get(event.getClass()));
        result.setProperty("base_url", getBaseUrl());
        if (event instanceof Edited) {
            result.setProperty("is_minor_edit", ((Edited) event).isMinorEdit());
            result.setProperty("version_comment", event.getContent().getVersionComment());
        }

        result.setProperty("creator", renderUserData(event.getContent().getCreator()));
        User user = findEventUser(event);
        if (user != null) {
            result.setProperty("user", renderUserData(user));
        }
        return result;
    }

    private static JsonObject renderContentJson(final ContentEntityObject content) {
        if (content instanceof Page) {
            return renderPageData((Page) content);
        } else if (content instanceof BlogPost) {
            return renderBlogData((BlogPost) content);
        } else if (content instanceof Comment) {
            return renderCommentData((Comment) content);
        } else {
            throw new RuntimeException("Unknown event content");
        }
    }

    private static JsonObject renderSpaceData(final Space space) {
        JsonObject result = new JsonObject();
        result.setProperty("name", space.getName());
        result.setProperty("key", space.getKey());
        result.setProperty("url", getBaseUrl() + space.getUrlPath());
        result.setProperty("type", space.getSpaceType().toString());
        result.setProperty("status", space.getSpaceStatus().name());
        result.setProperty("description", space.getDescription().getDisplayTitle());
        return result;
    }

    private static JsonObject renderBlogData(final BlogPost blog) {
        JsonObject result = renderObjectData(blog);
        result.setProperty("space", renderSpaceData(blog.getSpace()));
        return result;
    }

    private static JsonObject renderCommentData(final Comment comment) {
        JsonObject result = renderObjectData(comment);

        result.setProperty("display_title", comment.getDisplayTitle());
        result.setProperty("descendants_count", comment.getDescendantsCount());
        result.setProperty("depth", comment.getDepth());
        result.setProperty("is_inline_comment", comment.isInlineComment());
        result.setProperty("last_modifier", comment.getStatus().getLastModifier());
        result.setProperty("status", comment.getStatus().getValue().getStringValue());
        result.setProperty("thread_change_date", comment.getThreadChangedDate().getTime());

        if (comment.getParent() != null) {
            result.setProperty("parent", renderObjectData(comment.getParent()));
        }
        result.setProperty("container", renderContentJson(comment.getContainer()));
        return result;
    }

    private static JsonObject renderPageData(final Page page) {
        JsonObject result = renderObjectData(page);
        result.setProperty("tiny_url", getBaseUrl() + "/x/" + new TinyUrl(page).getIdentifier());
        result.setProperty("position", page.getPosition());
        result.setProperty("content_id", page.getContentId().asLong());
        result.setProperty("edit_url", getBaseUrl() + page.getEditUrlPath());
        result.setProperty("is_current", page.isCurrent());
        result.setProperty("is_deleted", page.isDeleted());
        result.setProperty("is_draft", page.isDraft());
        result.setProperty("is_unpublished", page.isUnpublished());
        result.setProperty("is_root_level", page.isRootLevel());
        result.setProperty("is_home_page", page.isHomePage());
        result.setProperty("is_indexable", page.isIndexable());

        JsonArray ancestors = new JsonArray();
        for (Page ancestor : page.getAncestors()) {
            JsonObject ancestorJson = new JsonObject();
            ancestorJson.setProperty("title", ancestor.getTitle());
            ancestorJson.setProperty("url", getBaseUrl() + ancestor.getUrlPath());
            ancestors.add(ancestorJson);
        }
        result.setProperty("ancestors", ancestors);

        result.setProperty("space", renderSpaceData(page.getSpace()));
        return result;
    }

    private static JsonObject renderObjectData(final ContentEntityObject object) {
        JsonObject result = new JsonObject();
        result.setProperty("title", object.getTitle());
        result.setProperty("content", object.getBodyAsStringWithoutMarkup());
        result.setProperty("html_content", renderAsHtml(object));
        result.setProperty("url", getBaseUrl() + object.getUrlPath());
        result.setProperty("id", String.valueOf(object.getId()));
        result.setProperty("type", object.getType());
        result.setProperty("excerpt", object.getExcerpt());
        result.setProperty("body", object.getBodyAsString());

        JsonArray labels = new JsonArray();
        for (Label label : object.getLabels()) {
            labels.add(new JsonString(label.getDisplayTitle()));
        }
        result.setProperty("labels", labels);
        return result;
    }

    private static JsonObject renderUserData(final User user) {
        JsonObject result = new JsonObject();
        result.setProperty("email", user.getEmail());
        result.setProperty("username", user.getName());
        result.setProperty("full_name", user.getFullName());
        return result;
    }

    private static String renderAsHtml(final ContentEntityObject object) {
        final DeviceTypeAwareRenderer renderer = (DeviceTypeAwareRenderer) ContainerManager
                .getComponent("viewRenderer");
        ConversionContext conversionContext = new DefaultConversionContext(object.toPageContext());
        return renderer.render(object.getEntity(), conversionContext);
    }

    private static String getBaseUrl() {
        String baseUrl = GeneralUtil.getGlobalSettings().getBaseUrl();
        if (TextUtils.stringSet(baseUrl) && baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    private static User findEventUser(final ContentEvent event) {
        if (event instanceof Created) {
            return event.getContent().getCreator();
        } else if (event instanceof Updated) {
            return event.getContent().getLastModifier();
        } else if (event instanceof com.atlassian.confluence.event.events.types.UserDriven) {
            return ((com.atlassian.confluence.event.events.types.UserDriven) event).getOriginatingUser();
        } else if (event instanceof com.atlassian.confluence.event.events.content.page.async.types.UserDriven) {
            UserAccessor userAccessor = (UserAccessor) ContainerManager.getComponent("userAccessor");
            UserKey userKey = ((com.atlassian.confluence.event.events.content.page.async.types.UserDriven) event)
                    .getOriginatingUserKey();
            return userAccessor.getExistingUserByKey(userKey);
        } else {
            return null;
        }
    }
}
