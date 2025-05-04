package dut.com.fastfooddatabase.ui.viewmodels;

import androidx.lifecycle.ViewModel;

import dut.com.fastfooddatabase.data.repository.MenuItemRepository;
import dut.com.fastfooddatabase.data.repository.ShopRepository;

public class SearchViewModel extends ViewModel {
    private final MenuItemRepository menuItemRepository;
    private final ShopRepository shopRepository;

}
