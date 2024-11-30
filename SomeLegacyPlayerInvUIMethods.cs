private void ReloadSlot(ItemStack stack, int index) {
  UISlot uiSlot = GetUISlotByIndex(index);
  bool containedStack = uiSlot != null;
  
  if(containedStack) {
      if(newEmpty || stack.item.GetItemType() == currentTab) {
          uiSlot.MarkDirty(true);
          ReorderMainInv();
          return;
      }
  
      DeactivateUISlot(uiSlot);
      return;
  }
  
  if(newEmpty || stack.item.GetItemType() == currentTab) {
      ActivateUISlot(index);
      return;
  }
  
  DeactivateUISlot();
  ReorderMainInv();
}

private void DeactivateUISlot() {
    UISlot lastActiveSlot = GetLastActiveUISlot();
    lastActiveSlot.Deactivate();
}

private void ActivateUISlot() {
    UISlot firstDeactivatedSlot = GetFirstDeactivatedUISlot();
    firstDeactivatedSlot.Activate();
}

private void ReorderMainInv() {
    List<int> previousEmpties = new();
    for(int i = PlayerInventory.BOW_SLOT + 1; i < uiSlots.Count; i++) {
        if(uiSlots[i].StackIndex == -1)
            return;


        if(inventory.GetStack(uiSlots[i].StackIndex).IsEmpty()) {
            previousEmpties.Add(i);
            continue;
        }

        if(previousEmpties.Count == 0)
            continue;

        uiSlots[previousEmpties[0]].Rebind(uiSlots[i + 1].StackIndex);
        uiSlots[previousEmpties[0]].MarkDirty(true);

        previousEmpties.RemoveAt(0);
    }
}
