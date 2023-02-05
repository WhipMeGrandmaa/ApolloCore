package me.whipmegrandma.apollocore.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import me.whipmegrandma.apollocore.settings.PlayerShopMenuSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.InventoryDrawer;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.*;

/**
 * An advanced menu listing items with automatic page support
 *
 * @param <T> the item that each page consists of
 */
public abstract class IntermediateMenuPagged<T> extends Menu {

	/**
	 * The active page button material, used in buttons to previous/next pages
	 * when they can be clicked (such as to go to the next/previous page)
	 * <p>
	 * Defaults to lime dye
	 */
	@Getter
	@Setter
	private static CompMaterial activePageButton = CompMaterial.LIME_DYE;

	/**
	 * The inactive page button material, used in buttons to previous/next
	 * pages when they cannot be clicked (i.e. on the first/last page)
	 * <p>
	 * Defaults to gray dye
	 */
	@Getter
	@Setter
	private static CompMaterial inactivePageButton = CompMaterial.GRAY_DYE;

	/**
	 * The slots the current page's items will be in
	 */
	@Getter
	private final List<Integer> slots;

	/**
	 * The raw items iterated
	 */
	private final Iterable<T> items;

	/**
	 * The page size overriding automatic pagination system adjusting menu
	 * size based on item count
	 */
	private final Integer manualPageSize;

	/**
	 * The pages by the page number, containing a list of items
	 */
	@Getter
	private final Map<Integer, List<T>> pages = new HashMap<>();

	/**
	 * The current page
	 */
	@Getter
	private int currentPage = 1;

	/**
	 * The next button automatically generated
	 */
	private Button nextButton;

	/**
	 * The "go to previous page" button automatically generated
	 */
	private Button prevButton;

	/**
	 * Create a new paged menu with automatic page size
	 *
	 * @param items
	 */
	protected IntermediateMenuPagged(@NonNull final T... items) {
		this(null, null, null, Arrays.asList(items), false);
	}

	/**
	 * Create a new paged menu with automatic page size
	 *
	 * @param items the pages
	 */
	protected IntermediateMenuPagged(final Iterable<T> items) {
		this(null, null, null, items, false);
	}

	/**
	 * Create a new paged menu with automatic page size
	 *
	 * @param parent the parent menu
	 * @param items  the pages the pages
	 */
	protected IntermediateMenuPagged(final Menu parent, @NonNull final T... items) {
		this(null, parent, null, Arrays.asList(items), false);
	}

	/**
	 * Create a new paged menu with automatic page size
	 *
	 * @param parent the parent menu
	 * @param items  the pages the pages
	 */
	protected IntermediateMenuPagged(final Menu parent, final Iterable<T> items) {
		this(null, parent, null, items, false);
	}

	/**
	 * Create a new paged menu with automatic page size
	 *
	 * @param slots the slots where the items should be placed on a page
	 * @param items the pages the pages
	 */
	protected IntermediateMenuPagged(final List<Integer> slots, final Iterable<T> items) {
		this(null, null, slots, items, false);
	}

	/**
	 * Create a new paged menu with automatic page size
	 *
	 * @param parent the parent menu
	 * @param slots  the slots where the items should be placed on a page
	 * @param items  the pages the pages
	 */
	protected IntermediateMenuPagged(final Menu parent, final List<Integer> slots, final Iterable<T> items) {
		this(null, parent, slots, items, false);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param parent                 the parent menu
	 * @param items                  the pages
	 * @param returnMakesNewInstance
	 */
	protected IntermediateMenuPagged(final Menu parent, final Iterable<T> items, final boolean returnMakesNewInstance) {
		this(null, parent, null, items, returnMakesNewInstance);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param parent                 the parent menu
	 * @param slots                  the slots where the items should be placed on a page
	 * @param items                  the pages
	 * @param returnMakesNewInstance
	 */
	protected IntermediateMenuPagged(final Menu parent, final List<Integer> slots, final Iterable<T> items, final boolean returnMakesNewInstance) {
		this(null, parent, slots, items, returnMakesNewInstance);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param pageSize size of the menu, a multiple of 9 (keep in mind we already add
	 *                 1 row there)
	 * @param items    the pages
	 */
	protected IntermediateMenuPagged(final int pageSize, @NonNull final T... items) {
		this(pageSize, null, null, Arrays.asList(items), false);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param pageSize size of the menu, a multiple of 9 (keep in mind we already add
	 *                 1 row there)
	 * @param items    the pages
	 */
	protected IntermediateMenuPagged(final int pageSize, final Iterable<T> items) {
		this(pageSize, null, null, items, false);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param pageSize size of the menu, a multiple of 9 (keep in mind we already add
	 *                 1 row there)
	 * @param items    the pages
	 */
	protected IntermediateMenuPagged(final int pageSize, final List<Integer> slots, final Iterable<T> items) {
		this(pageSize, null, slots, items, false);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param pageSize size of the menu, a multiple of 9 (keep in mind we already add
	 *                 1 row there)
	 * @param parent   the parent menu
	 * @param items    the pages the pages
	 */
	protected IntermediateMenuPagged(final int pageSize, final Menu parent, @NonNull T... items) {
		this(pageSize, parent, null, Arrays.asList(items), false);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param pageSize size of the menu, a multiple of 9 (keep in mind we already add
	 *                 1 row there)
	 * @param parent   the parent menu
	 * @param items    the pages the pages
	 */
	protected IntermediateMenuPagged(final int pageSize, final Menu parent, final Iterable<T> items) {
		this(pageSize, parent, null, items, false);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param pageSize
	 * @param parent
	 * @param items
	 * @param returnMakesNewInstance
	 */
	protected IntermediateMenuPagged(final int pageSize, final Menu parent, final Iterable<T> items, final boolean returnMakesNewInstance) {
		this(pageSize, parent, null, items, returnMakesNewInstance);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param pageSize               size of the menu, a multiple of 9 (keep in mind we already add
	 *                               1 row there)
	 * @param slots                  the slots where the items should be placed on a page
	 * @param parent                 the parent menu
	 * @param items                  the pages the pages
	 * @param returnMakesNewInstance should we re-instatiate the parent menu when returning to it?
	 */
	private IntermediateMenuPagged(final Integer pageSize, final Menu parent, final List<Integer> slots, final Iterable<T> items, final boolean returnMakesNewInstance) {
		super(parent, returnMakesNewInstance);

		this.slots = slots != null ? slots : new ArrayList<>();
		this.items = items;
		this.manualPageSize = pageSize;

		this.calculatePages();
		this.setButtons();
	}

	/*
	 * Recalculate pages
	 */
	private void calculatePages() {
		final int items = this.getItemAmount(this.items);
		final int autoPageSize;

		if (this.slots.isEmpty()) {
			autoPageSize = this.manualPageSize != null ? this.manualPageSize : items <= 9 ? 9 * 1 : items <= 9 * 2 ? 9 * 2 : items <= 9 * 3 ? 9 * 3 : items <= 9 * 4 ? 9 * 4 : 9 * 5;

			for (int i = 0; i < autoPageSize; i++)
				this.slots.add(i);

			this.setSize(9 + autoPageSize);

		} else
			autoPageSize = this.slots.size();

		this.pages.clear();
		this.pages.putAll(Common.fillPages(autoPageSize, this.items));
	}

	@SuppressWarnings("unused")
	private int getItemAmount(final Iterable<T> pages) {
		int amount = 0;

		for (final T t : pages)
			amount++;

		return amount;
	}

	// Render the next/prev buttons
	private void setButtons() {
		// Set previous button
		this.prevButton = this.currentPage > 1 ? this.formPreviousButton() : Button.makeSimple(ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE).name(" "), non -> {
		});

		// Set next page button
		this.nextButton = this.pages.size() > 1 && this.currentPage < this.pages.size() ? this.formNextButton() : Button.makeSimple(ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE).name(" "), non -> {
		});
	}

	/**
	 * Return the button to list the previous page,
	 * override to customize it.
	 *
	 * @return
	 */
	public Button formPreviousButton() {
		return new Button() {
			final boolean canGo = getCurrentPage() > 1;

			@Override
			public void onClickedInMenu(final Player player, final Menu menu, final ClickType click) {
				if (this.canGo)
					setCurrentPage(MathUtil.range(getCurrentPage() - 1, 1, getPages().size()));
			}

			@Override
			public ItemStack getItem() {
				final int previousPage = getCurrentPage() - 1;

				return ItemCreator
						.of(PlayerShopMenuSettings.previousPageMaterial)
						.name(PlayerShopMenuSettings.previousPageTitle)
						.make();
			}
		};
	}

	/**
	 * Return the button to list the next page,
	 * override to customize it.
	 *
	 * @return
	 */
	public Button formNextButton() {
		return new Button() {
			final boolean canGo = getCurrentPage() < getPages().size();

			@Override
			public void onClickedInMenu(final Player player, final Menu menu, final ClickType click) {
				if (this.canGo)
					setCurrentPage(MathUtil.range(getCurrentPage() + 1, 1, getPages().size()));
			}

			@Override
			public ItemStack getItem() {
				final boolean lastPage = getCurrentPage() == getPages().size();

				return ItemCreator
						.of(PlayerShopMenuSettings.nextPageMaterial)
						.name(PlayerShopMenuSettings.nextPageTitle)
						.make();
			}
		};
	}

	/**
	 * Shows items for the current page
	 *
	 * @param currentPage
	 */
	protected void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;

		this.updatePage();
	}

	// Reinits the menu and plays the anvil sound
	private void updatePage() {
		this.setButtons();
		this.restartMenu();

		Menu.getSound().play(this.getViewer());
		PlayerUtil.updateInventoryTitle(this.getViewer(), this.getTitle());
	}

	/**
	 * Automatically prepend the title with page numbers
	 * <p>
	 * Override for a custom last-minute implementation, but
	 * ensure to call the super method otherwise no title will
	 * be set in {@link InventoryDrawer}
	 *
	 * @param
	 */
	@Override
	protected final void onDisplay(final InventoryDrawer drawer) {
		drawer.setTitle(this.getTitle());

		this.onPostDisplay(drawer);
	}

	/**
	 * Called before the menu is displayed
	 *
	 * @param drawer
	 */
	protected void onPostDisplay(InventoryDrawer drawer) {
	}

	/**
	 * Return the {@link ItemStack} representation of an item on a certain page
	 * <p>
	 * Use {@link ItemCreator} for easy creation.
	 *
	 * @param item the given object, for example Arena
	 * @return the itemstack, for example diamond sword having arena name
	 */
	protected abstract ItemStack convertToItemStack(T item);

	/**
	 * Called automatically when an item is clicked
	 *
	 * @param player the player who clicked
	 * @param item   the clicked item
	 * @param click  the click type
	 */
	protected abstract void onPageClick(Player player, T item, ClickType click);

	/**
	 * Return true if you want our system to add page/totalPages suffix after
	 * your title, true by default
	 *
	 * @return
	 */
	protected boolean addPageNumbers() {
		return true;
	}

	/**
	 * Return if there are no items at all
	 *
	 * @return
	 */
	protected boolean isEmpty() {
		return this.pages.isEmpty() || this.pages.get(0).isEmpty();
	}

	/**
	 * Automatically get the correct item from the actual page, including
	 * prev/next buttons
	 * <p>
	 * WHEN OVERRIDING, MAKE SURE YOU CALL super.getItemAt OTHERWISE
	 * THIS MENU WILL NOT GENERATE PAGES NOR NAVIGATION BUTTONS
	 *
	 * @param slot the slot
	 * @return the item, or null
	 */
	@Override
	public ItemStack getItemAt(final int slot) {
		if (this.slots.contains(slot) && this.slots.indexOf(slot) < this.getCurrentPageItems().size()) {
			final T object = this.getCurrentPageItems().get(this.slots.indexOf(slot));

			if (object != null)
				return this.convertToItemStack(object);
		}

		if (slot == this.getPreviousButtonPosition())
			return this.prevButton.getItem();

		if (slot == this.getNextButtonPosition())
			return this.nextButton.getItem();

		return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE).name(" ").make();
	}

	/**
	 * Override to edit where the button to previous page is,
	 * defaults to "size of the menu - 6"
	 *
	 * @return
	 */
	protected int getPreviousButtonPosition() {
		return this.getSize() - 6;
	}

	/**
	 * Override to edit where the button to next page is,
	 * defaults to "size of the menu - 4"
	 *
	 * @return
	 */
	protected int getNextButtonPosition() {
		return this.getSize() - 4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void onMenuClick(final Player player, final int slot, final InventoryAction action, final ClickType click, final ItemStack cursor, final ItemStack clicked, final boolean cancelled) {
		if (this.slots.contains(slot) && this.slots.indexOf(slot) < this.getCurrentPageItems().size()) {
			final T obj = this.getCurrentPageItems().get(this.slots.indexOf(slot));

			if (obj != null) {
				final val prevType = player.getOpenInventory().getType();
				this.onPageClick(player, obj, click);

				if (prevType == player.getOpenInventory().getType())
					player.getOpenInventory().getTopInventory().setItem(slot, this.getItemAt(slot));
			}
		}
	}

	// Do not allow override
	@Override
	public final void onButtonClick(final Player player, final int slot, final InventoryAction action, final ClickType click, final Button button) {
		super.onButtonClick(player, slot, action, click, button);
	}

	// Do not allow override
	@Override
	public final void onMenuClick(final Player player, final int slot, final ItemStack clicked) {
		throw new FoException("Simplest click unsupported");
	}

	// Get all items in a page
	private List<T> getCurrentPageItems() {
		Valid.checkBoolean(this.pages.containsKey(this.currentPage - 1), "The menu has only " + this.pages.size() + " pages, not " + this.currentPage + "!");

		return this.pages.get(this.currentPage - 1);
	}
}